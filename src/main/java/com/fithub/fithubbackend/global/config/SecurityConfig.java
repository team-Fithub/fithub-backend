package com.fithub.fithubbackend.global.config;

import com.fithub.fithubbackend.global.auth.*;
import com.fithub.fithubbackend.global.util.HeaderUtil;
import com.fithub.fithubbackend.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 토큰 프로바이더 추가
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final HeaderUtil headerUtil;

    private final OAuthService oAuthService;
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final OAuthFailureHandler oAuthFailureHandler;

    private static final String[] PERMIT_ALL_PATTERNS = new String[] {
            "/", "/auth/**", "/oauth2/**"
    };

    private static final String[] PERMIT_ALL_GET_PATTERNS = new String[] {
        "/users/training/all", "/users/training", "/users/training/reviews", "/posts/public/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // jwtFilter 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisUtil, headerUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(jwtTokenProvider, headerUtil), JwtAuthenticationFilter.class)

                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                Arrays.stream(PERMIT_ALL_PATTERNS).map(AntPathRequestMatcher::antMatcher).toArray(AntPathRequestMatcher[]::new)
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET, PERMIT_ALL_GET_PATTERNS).permitAll()
//                        .requestMatchers("/**").hasRole("USER")
                        .requestMatchers("/trainer/training/**").hasRole("TRAINER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> {
                    e.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
                    e.accessDeniedHandler(new CustomAccessDeniedHandler());
                })
                .oauth2Login(oauth2Login -> {
                    oauth2Login.userInfoEndpoint(userInfoEndPoint -> userInfoEndPoint.userService(oAuthService));
                    oauth2Login.successHandler(oAuthSuccessHandler);
                    oauth2Login.failureHandler(oAuthFailureHandler);
                })
                .build();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setExposedHeaders(List.of("X-AUTH-TOKEN"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
