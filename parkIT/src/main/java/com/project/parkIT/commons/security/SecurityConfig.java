package com.project.parkIT.commons.security;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.parkIT.repository.owner.OwnerRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
	
	private final OwnerDetailsService ownerDetailsService;
	private final ObjectMapper objectMapper;
	private final OwnerRepository ownerRepository;
	private final JwtService jwtService;
	
	private final String[] allowUrls = {
				"/", 
				"/api/**",
				"/swagger-ui/**", "/v3/**", 
				"owner/join", "member/join", "owner/login", "member/login"
//				"/**/join", "/**/login"
			};
	
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
				.csrf(CsrfConfigurer<HttpSecurity>::disable)
//				.csrf(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				//필터 추가
				.addFilterAfter(usernamePasswordLoginFilter(), LogoutFilter.class)
				.addFilterBefore(jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
				
				.authorizeHttpRequests(requests -> 
					requests
					.requestMatchers(allowUrls).permitAll()
					.anyRequest().authenticated()
				)
//				.logout(AbstractHttpConfigurer::disable)
				.logout((logout) -> logout
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true))
				.sessionManagement(sessionManagement -> 
					sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.build();
	}
	
	//PasswordEncoder
	@Bean
	public PasswordEncoder passwordEncoder() {
		//DelegatingPasswordEncode: 여러 인코딩 알고리즘 사용 가능
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//		return new BCryptPasswordEncoder();
	}
	
	//인증 관리자 관련 설정 (AuthenticationProvider에 PasswordEncoder 등록)
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		
		daoAuthenticationProvider.setUserDetailsService(ownerDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return daoAuthenticationProvider;
	}
	
	//AuthenticationManager 등록
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		DaoAuthenticationProvider provider = daoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		
		return new ProviderManager(provider);
	}
	
	//커스텀 필터(UsernamePasswordAuthenticationFilter) 등록
	@Bean
	public UsernamePasswordAuthenticationFilter usernamePasswordLoginFilter() throws Exception {
		UsernamePasswordAuthenticationFilter usernamePasswordLoginFilter = new UsernamePasswordAuthenticationFilter(objectMapper);
		
		//AuthenticationManager도 함께 등록해야 함
		usernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
		
		//로그인 성공, 실패 핸들러 등록
		usernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProviderHandler());
		usernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
		
		return usernamePasswordLoginFilter;
	}
	
	//LoginSuccessJWTProviderHandler 등록
	@Bean
	public LoginSuccessJWTProviderHandler loginSuccessJWTProviderHandler() {
		return new LoginSuccessJWTProviderHandler(jwtService, ownerRepository);
	}
	
	//LoginFailureHandler 등록
	@Bean
	public LoginFailureHandler loginFailureHandler() {
		return new LoginFailureHandler();
	}
	
	@Bean
	public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
		JwtAuthenticationProcessingFilter jsonUsernamePasswordLoginFilter = new JwtAuthenticationProcessingFilter(jwtService, ownerRepository);
		
		return jsonUsernamePasswordLoginFilter;
	}
}
