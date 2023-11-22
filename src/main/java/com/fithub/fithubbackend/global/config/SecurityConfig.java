package com.fithub.fithubbackend.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // TODO: 토큰 프로바이더 추가

    // TODO: 로그인, 회원가입 패턴으로 수정
    private static final String[] PERMIT_ALL_PATTERNS = new String[] {
            "/", "/auth/**"
    };

    private static final String[] PERMIT_ALL_GET_PATTERNS = new String[] {
        "/users/training/**"
    };
    private static final String[] PERMIT_ALL_POST_PATTERNS = new String[] {
            "/signup", "/email/**"
    };
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // TODO: jwtFilter 추가
                // .addFilterBefore()
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                Arrays.stream(PERMIT_ALL_PATTERNS).map(AntPathRequestMatcher::antMatcher).toArray(AntPathRequestMatcher[]::new)
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET, PERMIT_ALL_GET_PATTERNS).permitAll()
                        .requestMatchers(
                                HttpMethod.POST, PERMIT_ALL_POST_PATTERNS).permitAll()
                        .anyRequest().authenticated()
                )
                // TODO: exceptionHandling, oauth2 설정 추가
                .build();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**");
    }

    //passwordEncoder
    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }
}
