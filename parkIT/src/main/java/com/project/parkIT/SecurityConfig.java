package com.project.parkIT;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final String[] allowUrls = {"/", "/swagger-ui/**", "/v3/**", "owner/sign-up", "/sign-in"};
//	private final JwtTokenProvider jwtTokenProvider;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
//		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(CsrfConfigurer<HttpSecurity>::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(requests -> 
					requests
					.requestMatchers("owner/sign-up", "member/sign-up", "owner/sign-in", "member/sign-in").permitAll()
					.requestMatchers("/", "/swagger-ui/**", "/v3/**").permitAll()
//					.requestMatchers(allowUrls).permitAll()
					.anyRequest().authenticated()
				)
				.sessionManagement(sessionManagement -> 
					sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.build();
	}
	
}
