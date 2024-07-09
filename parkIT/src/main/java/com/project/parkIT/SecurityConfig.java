package com.project.parkIT;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	private final String[] allowUrls = {
				"/", 
				"/api/**",
				"/swagger-ui/**", "/v3/**", 
				"owner/sign-up", "member/sign-up", "owner/sign-in", "member/sign-in"
			};
//	private final JwtTokenProvider jwtTokenProvider;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
//		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return new BCryptPasswordEncoder();
	}
	
	//CORS 설정
	CorsConfigurationSource corsConfigurationSource() {
		return request -> {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedHeaders(Collections.singletonList("*"));
			config.setAllowedMethods(Collections.singletonList("*"));
			
			//허용할 origin
			config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:3000"));
			
			config.setAllowCredentials(true);
			return config;
		};
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.httpBasic(HttpBasicConfigurer::disable)
				.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
//				.csrf(CsrfConfigurer<HttpSecurity>::disable)
				.csrf(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.logout(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(requests -> 
					requests
					.requestMatchers(allowUrls).permitAll()
					.anyRequest().authenticated()
				)
				.sessionManagement(sessionManagement -> 
					sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.build();
	}
	
}
