package com.project.parkIT.commons.security;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface JwtService {

	String createAccessToken(String id);
	String createRefreshToken();
	
	void updateRefreshToken(String id, String refreshToken);
	void destroyRefreshToken(String id);
	
	void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken);
	void sendAccessToken(HttpServletResponse response, String accessToken);
	
	Optional<String> extractAccessToken(HttpServletRequest request);
	Optional<String> extractRefreshToken(HttpServletRequest request);
	Optional<String> extractId(String accessToken);
	
	void setAccessTokenHeader(HttpServletResponse response, String accessToken);
	void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);
	
	boolean isTokenValid(String token);
	
}
