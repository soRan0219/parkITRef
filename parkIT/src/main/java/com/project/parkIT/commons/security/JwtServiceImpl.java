package com.project.parkIT.commons.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.parkIT.repository.owner.OwnerRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtServiceImpl implements JwtService {

	//jwt.yml에 설정된 값 가져오기
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.access.expiration}")
	private long accessTokenValidityInSeconds;
	
	@Value("${jwt.refresh.expiration}")
	private long refreshTokenValidityInSeconds;
	
	@Value("${jwt.access.header}")
	private String accessHeader;
	
	@Value("${jwt.refresh.header}")
	private String refreshHeader;
	
	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private static final String USERNAME_CLAIM = "id";
	private static final String BEARER = "Bearer";
	
	private final OwnerRepository ownerRepository;
	private final ObjectMapper objectMapper;
	
	@Override
	public String createAccessToken(String id) {
		String accessToken = JWT.create()
				.withSubject(ACCESS_TOKEN_SUBJECT)
				.withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds*1000))
				.withClaim(USERNAME_CLAIM, id)
				.sign(Algorithm.HMAC512(secret));
		
		log.debug("** accessToken: " + accessToken);
		
		return accessToken;
	}

	@Override
	public String createRefreshToken() {
		String refreshToken =  JWT.create()
				.withSubject(REFRESH_TOKEN_SUBJECT)
				.withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds*1000))
				.sign(Algorithm.HMAC512(secret));
		
		log.debug("** refreshToken: " + refreshToken);
		
		return refreshToken;
	}

	@Override
	public void updateRefreshToken(String id, String refreshToken) {
		ownerRepository.findById(id)
			.ifPresentOrElse(owner -> owner.updateRefreshToken(refreshToken),
					() -> new Exception("회원 조회 실패"));
	}

	@Override
	public void destroyRefreshToken(String id) {
		ownerRepository.findById(id)
			.ifPresentOrElse(owner -> owner.destroyRefreshToken(),
				() -> new Exception("회원 조회 실패"));
	}

	@Override
	public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		
		setAccessTokenHeader(response, accessToken);
		setRefreshTokenHeader(response, refreshToken);
		
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
		tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);
	}

	@Override
	public void sendAccessToken(HttpServletResponse response, String accessToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		
		setAccessTokenHeader(response, accessToken);
		
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
	}

	@Override
	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(accessHeader))
				.filter(accessToken -> accessToken.startsWith(BEARER))
				.map(accessToken -> accessToken.replace(BEARER, ""));
	}

	@Override
	public Optional<String> extractRefreshToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(refreshHeader))
				.filter(refreshToken -> refreshToken.startsWith(BEARER))
				.map(refreshToken -> refreshToken.replace(BEARER, ""));
	}

	@Override
	public Optional<String> extractId(String accessToken) {
		try {
			return Optional
					.ofNullable(
							JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(USERNAME_CLAIM).asString()
							);
		} catch(Exception e) {
			log.error(e.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
		response.setHeader(accessHeader, accessToken);
	}

	@Override
	public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
		response.setHeader(refreshHeader, refreshToken);
	}

	@Override
	public boolean isTokenValid(String token) {
		try {
			JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
			return true;
		} catch(Exception e) {
			log.error("유효하지 않은 Token 입니다. ", e.getMessage());
			return false;
		}
	}

}
