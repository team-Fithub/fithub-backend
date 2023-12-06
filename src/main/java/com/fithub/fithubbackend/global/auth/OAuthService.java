package com.fithub.fithubbackend.global.auth;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 로그인 진행중인 서비스 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        User user = OAuthAttributes.extract(registrationId, attributes);
        user = saveOrUpdate(user,registrationId);

        Map<String, Object> customAttribute = customAttribute(attributes, userNameAttributeName, user);
        return new DefaultOAuth2User(
                user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList()),
                customAttribute,
                userNameAttributeName
        );
    }

    private Map<String, Object> customAttribute(Map<String, Object> attributes, String userNameAttributeName, User user) {
        Map<String, Object> customAttribute = new LinkedHashMap<>();
        customAttribute.put(userNameAttributeName, attributes.get(userNameAttributeName));
        customAttribute.put("id", user.getId());
        customAttribute.put("email", user.getEmail());
        customAttribute.put("provider", user.getProvider());
        customAttribute.put("nickname", user.getNickname());
        return customAttribute;
    }

    private User saveOrUpdate(User user, String registrationId) {
        // 회원가입에서는 GUEST 권한 부여, 추가 정보 입력 후 GUEST 권한 삭제, USER 권한 부여
        User newUser = userRepository.findByEmailAndProvider(user.getEmail(), user.getProvider())
                .map(m -> m.updateNicknameAndEmail(user.getNickname(), user.getEmail()))
                .orElseGet(() -> {
                    if(registrationId.equals("google")) {
                        return ofGoogle(user);
                    }
                    else if(registrationId.equals("kakao")){
                        return ofKakao(user);
                    }
                    else
                        return ofNaver(user);
                });
        return userRepository.save(newUser);
    }
    private User ofGoogle(User user) {
        return User.oAuthBuilder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .oAuthBuild();
    }
    private User ofKakao(User user) {
        return User.oAuthKakaoBuilder()
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .oAuthKakaoBuild();
    }
    private User ofNaver(User user) {
        return user;
    }
}
