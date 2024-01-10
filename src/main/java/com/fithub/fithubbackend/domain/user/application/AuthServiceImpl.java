package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.*;
import com.fithub.fithubbackend.domain.user.dto.constants.SignUpDtoConstants;
import com.fithub.fithubbackend.domain.user.repository.DocumentRepository;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.auth.JwtTokenProvider;
import com.fithub.fithubbackend.global.auth.TokenInfoDto;
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

        String profileImgInputName = "default";
        String profileImgPath = "profiles/default";

        if(profileImg != null && !profileImg.isEmpty()){
            profileImgPath = awsS3Uploader.imgPath("profiles");
            profileImgUrl = awsS3Uploader.putS3(profileImg,profileImgPath);
            profileImgInputName = profileImg.getOriginalFilename();
        }

        Document document = Document.builder()
                        .url(profileImgUrl)
                        .inputName(profileImgInputName)
                        .path(profileImgPath)
                        .build();

        documentRepository.save(document);
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword()); // 비밀번호 인코딩

        User user = User.builder().signUpDto(signUpDto).encodedPassword(encodedPassword).document(document).build();
        userRepository.save(user);
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
    public void signOut(SignOutDto signOutDto, HttpServletResponse response, HttpServletRequest request) {

        try {
            jwtTokenProvider.validateToken(signOutDto.getAccessToken());
        } catch (JwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN, "검증되지 않는 토큰이므로 로그아웃 실패");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(signOutDto.getAccessToken());

        if (redisUtil.getData(authentication.getName()) != null) {
            redisUtil.deleteData(signOutDto.getEmail());
        }
        else {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN, "redis에 토큰이 존재하지 않습니다.");
        }

        Long expiration = jwtTokenProvider.getExpiration(signOutDto.getAccessToken());
        redisUtil.setData(signOutDto.getAccessToken(), "logout", expiration);

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
    public String oAuthSignUp(OAuthSignUpDto oAuthSignUpDto, HttpServletResponse response) {
        User user = userRepository.findByProviderId(oAuthSignUpDto.getProviderId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당 회원을 찾을 수 없습니다. 소셜 회원가입을 다시 진행해주십시오."));
        user.setOAuthSignUp(oAuthSignUpDto);
        user.updateGuestToUser();

        TokenInfoDto tokenInfoDto = jwtTokenProvider.createToken(oAuthSignUpDto.getEmail());

        // refreshToken 쿠키에 저장
        cookieUtil.addRefreshTokenCookie(response, tokenInfoDto);
        // Redis에 Key(이메일):Value(refreshToken) 저장
        redisUtil.setData(oAuthSignUpDto.getEmail(), tokenInfoDto.getRefreshToken(), tokenInfoDto.getRefreshTokenExpirationTime());
        return tokenInfoDto.getAccessToken();
    }

    private void duplicateNickname(String nickname){
        if(userRepository.findByNickname(nickname).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE,"중복된 닉네임 입니다.");
    }
    private void duplicateEmail(String email){
        if(userRepository.findByEmail(email).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE,"중복된 이메일 입니다.");
    }
    private void formValidate(BindingResult bindingResult){
        String message = String.valueOf(bindingResult.getFieldErrors().stream()
                        .findFirst().map(DefaultMessageSourceResolvable::getDefaultMessage))
                .replaceAll(SignUpDtoConstants.FORM_DATA_ERROR_REGEXP,"");
        if (bindingResult.hasErrors()) {
            throw new CustomException(ErrorCode.INVALID_FORM_DATA,message);
        }
    }
}
