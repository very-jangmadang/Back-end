package com.example.demo.config;
import com.example.demo.security.CustomAuthenticationEntryPoint;
import com.example.demo.security.jwt.JWTUtil;
import com.example.demo.security.jwt.JwtAuthenticationFilter;
import com.example.demo.security.oauth.OAuthLoginFailureHandler;
import com.example.demo.security.oauth.OAuthLoginSuccessHandler;
import com.example.demo.service.general.impl.CustomOidcUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    // 소셜 로그인
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomOidcUserService customOidcUserService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .httpBasic(HttpBasicConfigurer::disable)

                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable)

                .addFilterBefore( jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // 모든 OPTIONS 요청 허용
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                            .requestMatchers("/api/permit/**", "/login/**", "/nickname/**", "/home/**").permitAll()
                            .requestMatchers("/favicon.ico", "/static/**").permitAll() // 인증 없이 허용
                            .requestMatchers("/payment/**", "/payment/create/**", "/payment/approve/**", "/payment/redirect/**", "/index.html", "/hello.html/**").permitAll() // 인증 없이 허용 - yoon 테스트
                            .anyRequest().authenticated();
                })

                .oauth2Login(oauth -> {
                    oauth
                            //기본 OAuthUserService아닌 OidcUserService로 설정
                            .userInfoEndpoint(userInfo -> userInfo
                                    .oidcUserService(customOidcUserService)
                            )
                            // 처리한 정보는 SecurityContext에 OAuth2User로 기록되어있음
                            .successHandler(oAuthLoginSuccessHandler) // 로그인 성공시 수행
                            .failureHandler(oAuthLoginFailureHandler); // 로그인 실패시 수행
                });
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(customAuthenticationEntryPoint));
        return httpSecurity.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration configuration = new CorsConfiguration();

            configuration.setAllowedOrigins(Arrays.asList("https://beta.jangmadang.site", "https://www.jangmadang.site", "https://jangmadang.site", "https://api.jangmadang.site", "http://localhost:5173", "https://api.beta.jangmadang.site"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
            configuration.setAllowCredentials(true);
            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
            configuration.setMaxAge(3600L);

            configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "access", "refresh", "idToken"));
            return configuration;
        };
    }
}
