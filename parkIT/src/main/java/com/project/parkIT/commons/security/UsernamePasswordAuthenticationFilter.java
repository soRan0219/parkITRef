package com.project.parkIT.commons.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	// ../oauth/login으로 오는 요청 처리
	private static final String DEFAULT_LOGIN_REQUEST_URL = "/**/login";
	//HTTP 메서드는 POST 방식
	private static final String HTTP_METHOD = "POST";
	//json 타입으로만 로그인
	private static final String CONTENT_TYPE = "application/json";
	
	private final ObjectMapper objectMapper;
	private static final String USERNAME_KEY = "id";
	private static final String PASSWORD_KEY = "pw";
	
	private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER = 
			new AntPathRequestMatcher(DEFAULT_LOGIN_REQUEST_URL, HTTP_METHOD);
	
	public UsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
		// ../oauth/login에 GET으로 오는 요청 처리하기 위함
		super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
		this.objectMapper = objectMapper;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		
//		log.debug(" @@@ @@@ *** *** UsernamePasswordAuthenticationFilter");
		
		if(request.getContentType()==null || !request.getContentType().equals(CONTENT_TYPE)) {
			throw new AuthenticationServiceException("Authentication Content-Type not supported: " + request.getContentType());
		}
		
		String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
		
		Map<String, String> usernamePasswordMap = objectMapper.readValue(messageBody, Map.class);
		
		String username = usernamePasswordMap.get(USERNAME_KEY);
		String password = usernamePasswordMap.get(PASSWORD_KEY);
		
//		log.debug(" @@@ @@@ *** *** username: " + username);
//		log.debug(" @@@ @@@ *** *** password: " + password);
		
		//principal과 credentials 전달
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		
		return this.getAuthenticationManager().authenticate(authRequest);
	}

}
