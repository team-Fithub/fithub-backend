package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.domain.UserInterest;
import com.fithub.fithubbackend.domain.user.dto.*;
import com.fithub.fithubbackend.domain.user.dto.constants.SignUpDtoConstants;
import com.fithub.fithubbackend.domain.user.repository.DocumentRepository;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.auth.JwtTokenProvider;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
import com.fithub.fithubbackend.global.common.Category;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.domain.Document;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.CookieUtil;
import com.fithub.fithubbackend.global.util.HeaderUtil;
import com.fithub.fithubbackend.global.util.RedisUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;
    private final HeaderUtil headerUtil;

    private final AwsS3Uploader awsS3Uploader;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Value("${default.image.address}")
    private String profileImgUrl;

    private static final String BEARER_TYPE = "Bearer ";

    private static final String AUTHORIZATION_HEADER = "Authorization";


    @Override
    @Transactional
    public ResponseEntity<SignUpResponseDto> signUp(SignUpDto signUpDto, MultipartFile profileImg, BindingResult bindingResult) throws IOException {
        formValidate(bindingResult); // 입력 형식 검증
        duplicateEmail(signUpDto.getEmail()); // 이메일 중복 확인
        duplicateNickname(signUpDto.getNickname()); // 닉네임 중복 확인

        Document document = null;
        
        if(profileImg != null && !profileImg.isEmpty()){
            String profileImgPath = awsS3Uploader.imgPath("profiles");
            profileImgUrl = awsS3Uploader.putS3(profileImg,profileImgPath);
            String profileImgInputName = profileImg.getOriginalFilename();

            document = Document.builder()
                    .url(profileImgUrl)
                    .inputName(profileImgInputName)
                    .path(profileImgPath)
                    .build();

            documentRepository.save(document);
        }
        else {
            Optional<Document> defaultDocument = documentRepository.findById(1L);
            if (defaultDocument.isPresent()) 
                document = defaultDocument.get();
        }

        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword()); // 비밀번호 인코딩

        User user = User.builder().signUpDto(signUpDto).encodedPassword(encodedPassword).document(document).build();
        userRepository.save(user);
        saveUserInterests(signUpDto.getInterests(), user);

        SignUpResponseDto response = SignUpResponseDto.builder().user(user).build();

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public TokenInfoDto signIn(SignInDto signInDto, HttpServletResponse response) {

        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(authentication);

            // refreshToken 쿠키에 저장
            cookieUtil.addRefreshTokenCookie(response, tokenInfoDto);

            // Redis에 Key(이메일):Value(refreshToken) 저장
            redisUtil.setData(authentication.getName(), tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());

            response.setHeader(AUTHORIZATION_HEADER, BEARER_TYPE + tokenInfoDto.getAccessToken());
            return tokenInfoDto;
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.INVALID_PWD);
        }

    }

    @Override
    @Transactional
    public void signOut(HttpServletResponse response, HttpServletRequest request) {

        String accessToken = headerUtil.resolveAccessToken(request);

        if (accessToken == null)
            throw new CustomException(ErrorCode.NOT_FOUND, "헤더에 access Token 존재하지 않음");

        try {
            jwtTokenProvider.validateToken(accessToken);
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

            if (redisUtil.getData(authentication.getName()) != null)
                redisUtil.deleteData(authentication.getName());

            Long expiration = jwtTokenProvider.getExpiration(accessToken);
            redisUtil.setData(accessToken, "logout", expiration);

        } catch (JwtException jwtException) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "검증되지 않는 access Token");
        }
    }

    @Override
    @Transactional
    public TokenInfoDto reissue(String cookieRefreshToken, HttpServletRequest request, HttpServletResponse response) {
        // 1. Request Header 에서 Access Token 추출
        String accessToken = headerUtil.resolveAccessToken(request);

        // 2. Refresh Token 검증
        try{
            jwtTokenProvider.validateToken(cookieRefreshToken);
        } catch (JwtException e){
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN, "검증되지 않는 토큰이므로 토큰 재발급 실패");
        }

        // 3. Access Token 에서 User email 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        // 4. Redis 에서 User email 을 기반으로 저장된 Refresh Token 추출
        String refreshToken = redisUtil.getData(authentication.getName());

        if(refreshToken == null) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN, "redis에 토큰이 존재하지 않습니다.");
        }
        if(!refreshToken.equals(cookieRefreshToken)) {
            throw  new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN, "쿠키의 refresh Token과 redis의 refresh Token이 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(authentication);
        // 6. RefreshToken Redis 업데이트
        redisUtil.setData(authentication.getName(), tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());
        // 7. Cookie 업데이트
        cookieUtil.updateCookie(request, response, tokenInfoDto);

        return tokenInfoDto;
    }

    @Override
    @Transactional
    public void oAuthSignUp(OAuthSignUpDto oAuthSignUpDto, HttpServletResponse response) {
        User user = userRepository.findByProviderId(oAuthSignUpDto.getProviderId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 회원을 찾을 수 없습니다. 소셜 회원가입을 다시 진행해주십시오."));
        user.setOAuthSignUp(oAuthSignUpDto);
        user.updateGuestToUser();
        saveUserInterests(oAuthSignUpDto.getInterests(), user);

        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(oAuthSignUpDto.getEmail());

        // refreshToken 쿠키에 저장
        cookieUtil.addRefreshTokenCookie(response, tokenInfoDto);
        // Redis에 Key(이메일):Value(refreshToken) 저장
        redisUtil.setData(oAuthSignUpDto.getEmail(), tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());

        response.setHeader(AUTHORIZATION_HEADER, BEARER_TYPE + tokenInfoDto.getAccessToken());
    }

    @Override
    public void oAuthLogin(oAuthSignInDto dto, HttpServletResponse response) {
        String email = dto.getEmail();
        if (!userRepository.existsByEmailAndProvider(email, dto.getProvider())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "해당 provider로 가입된 이메일이 아닙니다.");
        }
        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(email);

        // refreshToken 쿠키에 저장
        cookieUtil.addRefreshTokenCookie(response, tokenInfoDto);
        // Redis에 Key(이메일):Value(refreshToken) 저장
        redisUtil.setData(email, tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());

        response.setHeader(AUTHORIZATION_HEADER, BEARER_TYPE + tokenInfoDto.getAccessToken());
    }

    @Override
    @Transactional
    public void updatePassword(PasswordUpdateDto passwordUpdateDto) {
        User user = userRepository.findByEmail(passwordUpdateDto.getEmail()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 회원"));

        if (passwordEncoder.matches(passwordUpdateDto.getPassword(), user.getPassword()))
            throw new CustomException(ErrorCode.BAD_REQUEST, "기존 비밀번호와 동일하므로 다른 비밀번호로 변경 필요");

        user.updatePassword(passwordEncoder.encode(passwordUpdateDto.getPassword()));

    }

    private void duplicateNickname(String nickname){
        if(userRepository.existsByNickname(nickname))
            throw new CustomException(ErrorCode.DUPLICATE,"중복된 닉네임 입니다.");
    }
    private void duplicateEmail(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getProvider() != null && !user.getProvider().isEmpty()) {
                throw new CustomException(ErrorCode.DUPLICATE, "해당 이메일은 " + user.getProvider() + " 소셜 로그인이 진행된 이메일입니다.");
            }
            throw new CustomException(ErrorCode.DUPLICATE,"중복된 이메일 입니다.");
        }
    }

    private void formValidate(BindingResult bindingResult){
        String message = String.valueOf(bindingResult.getFieldErrors().stream()
                        .findFirst().map(DefaultMessageSourceResolvable::getDefaultMessage))
                .replaceAll(SignUpDtoConstants.FORM_DATA_ERROR_REGEXP,"");
        if (bindingResult.hasErrors()) {
            throw new CustomException(ErrorCode.INVALID_FORM_DATA,message);
        }
    }

    private void saveUserInterests(List<Category> interests, User user) {
        interests.forEach(interest -> {
            UserInterest userInterest = UserInterest.builder().interest(interest).user(user).build();
            user.addInterest(userInterest);
        });
    }
}
