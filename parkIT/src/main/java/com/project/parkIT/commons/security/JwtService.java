package com.project.parkIT.commons.security;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.project.parkIT.domain.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {

	String createAccessToken(User user);
	String createAccessToken(Authentication auth);
	String createRefreshToken(Authentication auth);
	
	void updateRefreshToken(Authentication auth, String refreshToken);
	void destroyRefreshToken(Authentication auth);
	
	void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken);
	void sendAccessToken(HttpServletResponse response, String accessToken);
	
	Optional<String> extractAccessToken(HttpServletRequest request);
	Optional<String> extractRefreshToken(HttpServletRequest request);
	Optional<String> extractId(String accessToken);
	Optional<String> extractRole(String token);
	
	void setAccessTokenHeader(HttpServletResponse response, String accessToken);
//	void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);
	
	boolean isTokenValid(String token);
	
}
