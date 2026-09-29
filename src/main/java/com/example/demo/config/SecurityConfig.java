package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)  // Rest Api에서는 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // LogIn페이지 비활성화
                .httpBasic(Customizer.withDefaults()) // 또는 JWT등 다른 인증 방식 사용
                .authorizeHttpRequests(auth->auth
//                        .requestMatchers("/api/public/**").permitAll()  // 공개 APi
//                        .requestMatchers("/api/auth/**").permitAll()    // 회원 가입
//                    .requestMatchers("/api/private/**").authenticated() // 인증 필요
                        .requestMatchers("/api/auth/**").permitAll() // 회원 가입
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")  // 공개 APi
                        .requestMatchers("/api/private/**").hasAnyRole("USER", "ADMIN") // 인증 필요
                        .anyRequest().denyAll()) // 기본 인증(JWT로 대체 예정)
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class); // 추가

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
