package com.project.parkIT.commons.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.parkIT.domain.Member;
import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.User;
import com.project.parkIT.domain.enums.Role;
import com.project.parkIT.repository.member.MemberRepository;
import com.project.parkIT.repository.owner.OwnerRepository;

import jakarta.servlet.http.Cookie;
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
	private static final String AUTHORITIES_CLAIM = "auth";
	private static final String BEARER = "Bearer";
	
	private final OwnerRepository ownerRepository;
	private final MemberRepository memberRepository;
	private final ObjectMapper objectMapper;
	private final CookieUtil cookieUtil;
	
	@Override
	public String createAccessToken(Authentication authentication) {
		String id = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
		
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
//		log.debug(" createAccessToken() - Access authorities :" + authorities + "+++++ # @ $ % +++++ ");
		
		String accessToken = JWT.create()
				.withSubject(ACCESS_TOKEN_SUBJECT)
				.withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds*1000))
				.withClaim(USERNAME_CLAIM, id)
				.withClaim(AUTHORITIES_CLAIM, authorities)
				.sign(Algorithm.HMAC512(secret));
		
		log.debug(" createAccessToken() - accessToken: " + accessToken);
		
		return accessToken;
	}
	
	@Override
	public String createAccessToken(User user) {
		String id = user.getId();
		String authorities = "";
		
		if(user instanceof Owner) {
			authorities = ownerRepository.findById(id).get().getRole();
		} else if(user instanceof Member) {
			authorities = memberRepository.findById(id).get().getRole();
		}
		
		String accessToken = JWT.create()
				.withSubject(ACCESS_TOKEN_SUBJECT)
				.withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds*1000))
				.withClaim(USERNAME_CLAIM, id)
				.withClaim(AUTHORITIES_CLAIM, authorities)
				.sign(Algorithm.HMAC512(secret));
		
		log.debug(" createAccessToken() - accessToken: " + accessToken);
		
		return accessToken;
	}

	@Override
	public String createRefreshToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
		
		String refreshToken =  JWT.create()
				.withSubject(REFRESH_TOKEN_SUBJECT)
				.withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds*1000))
				.withClaim(AUTHORITIES_CLAIM, authorities)
				.sign(Algorithm.HMAC512(secret));
		
		log.debug(" createRefreshToken() - refreshToken: " + refreshToken);
		
		return refreshToken;
	}

	@Override
	public void updateRefreshToken(Authentication authentication, String refreshToken) {
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
		
		String id = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
		
		if(authorities.contains(Role.OWNER.name())) {
			ownerRepository.findById(id)
				.ifPresentOrElse(owner -> owner.updateRefreshToken(refreshToken),
					() -> new Exception("점주 조회 실패"));
		} else if(authorities.contains(Role.MEMBER.name())) {
			memberRepository.findById(id)
				.ifPresentOrElse(member -> member.updateRefreshToken(refreshToken),
					() -> new Exception("회원 조회 실패"));
		}
		
	}

	@Override
	public void destroyRefreshToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));
		
		String id = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
		
		if(authorities.contains(Role.OWNER.name())) {
			ownerRepository.findById(id)
			.ifPresentOrElse(owner -> owner.destroyRefreshToken(),
					() -> new Exception("점주 조회 실패"));
		} else if(authorities.contains(Role.MEMBER.name())) {
			memberRepository.findById(id)
			.ifPresentOrElse(member -> member.destroyRefreshToken(),
					() -> new Exception("회원 조회 실패"));
		}
	}

	@Override
	public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		
		setAccessTokenHeader(response, accessToken);
		cookieUtil.addCookie(response, "refreshToken", refreshToken, (int)refreshTokenValidityInSeconds);
//		setRefreshTokenHeader(response, refreshToken);
		
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
		tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);
	}

	@Override
	public void sendAccessToken(HttpServletResponse response, String accessToken) {
		response.setStatus(HttpServletResponse.SC_OK);
		
		setAccessTokenHeader(response, accessToken);
//		cookieUtil.addCookie(response, "accessToken", accessToken, accessTokenValidityInSeconds);
		
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
	}

	@Override
	public Optional<String> extractAccessToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(accessHeader))
				.filter(accessToken -> accessToken.startsWith(BEARER))
				.map(accessToken -> accessToken.replace(BEARER, ""));
//		return cookieUtil.getCookie(request, "accessToken").map(Cookie::getValue);
	}

	@Override
	public Optional<String> extractRefreshToken(HttpServletRequest request) {
//		return Optional.ofNullable(request.getHeader(refreshHeader))
//				.filter(refreshToken -> refreshToken.startsWith(BEARER))
//				.map(refreshToken -> refreshToken.replace(BEARER, ""));
		return cookieUtil.getCookie(request, "refreshToken").map(Cookie::getValue);
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
	public Optional<String> extractRole(String token) {
		try {
			return Optional
					.ofNullable(
							JWT.require(Algorithm.HMAC512(secret)).build().verify(token).getClaim(AUTHORITIES_CLAIM).asString()
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

//	@Override
//	public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
//		response.setHeader(refreshHeader, refreshToken);
//	}

	@Override
	public boolean isTokenValid(String token) {
		try {
			JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
			log.debug("유효한 Token 입니다.");
			return true;
		} catch(Exception e) {
			log.error("유효하지 않은 Token 입니다. ", e.getMessage());
			return false;
		}
	}

}
