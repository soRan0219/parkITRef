package com.project.parkIT.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.parkIT.commons.security.JwtService;
import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.enums.Role;
import com.project.parkIT.repository.owner.OwnerRepository;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
public class JwtServiceTest {

	@Autowired JwtService jwtService;
	@Autowired OwnerRepository ownerRepository;
	@Autowired EntityManager em;
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.access.header}")
	private String accessHeader;
	
	@Value("${jwt.refresh.header}")
	private String refreshHeader;
	
	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
	private static final String USERNAME_CLAIM = "id";
	private static final String BEARER = "Bearer";
	
	private String username = "username";
	
	@BeforeEach
	public void init() {
		Owner owner = Owner.builder()
				.id(username)
				.pw("12345")
				.name("TestOwner")
				.email("owner@naver.com")
				.tel("000-0000-0000")
				.role(Role.OWNER)
				.build();
		
		ownerRepository.save(owner);
		clear();
	}
	
	private void clear() {
		em.flush();
		em.clear();
	}
	
	private DecodedJWT getVerify(String token) {
		return JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
	}
	
	//토큰 header에 넣어서 전송
	private HttpServletRequest setRequest(String accessToken, String refreshToken) throws IOException {
		MockHttpServletResponse response = new MockHttpServletResponse();
		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
		
		String headerAccessToken = response.getHeader(accessHeader);
		String headerRefreshToken = response.getHeader(refreshHeader);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		request.addHeader(accessHeader, BEARER + headerAccessToken);
		request.addHeader(refreshHeader, BEARER + headerRefreshToken);
		
		return request;
	}
	
	@Test
	public void 액세스토큰발급() throws Exception {
		//given 
		//when
		String accessToken = jwtService.createAccessToken(username);
		
		DecodedJWT verify = getVerify(accessToken);
		
		String subject = verify.getSubject();
		String findUsername = verify.getClaim(USERNAME_CLAIM).asString();
		
		//then
		assertThat(findUsername).isEqualTo(username);
		assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
	}
	
	@Test
	public void 리프레쉬토큰발급() throws Exception {
		//given
		//when
		String refreshToken = jwtService.createRefreshToken();
		
		DecodedJWT verify = getVerify(refreshToken);
		
		String subject = verify.getSubject();
		String findUsername = verify.getClaim(USERNAME_CLAIM).asString();
		
		//then
		assertThat(findUsername).isNull();
		assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
	}
	
	@Test
	public void 리프레쉬토큰갱신() throws Exception {
		//given
		String refershToken = jwtService.createRefreshToken();
		jwtService.updateRefreshToken(username, refershToken);
		clear();
		//refreshToken 똑같이 발급될 수 있으므로 3초 sleep
		Thread.sleep(3000);
		
		//when
		String reissuedRefreshToken = jwtService.createRefreshToken();
		jwtService.updateRefreshToken(username, reissuedRefreshToken);
		clear();
		
		//then
		assertThrows(Exception.class, () -> ownerRepository.findByRefreshToken(refershToken).get());
		assertThat(ownerRepository.findByRefreshToken(reissuedRefreshToken).get().getId()).isEqualTo(username);
	}
	
	@Test
	public void 리프레쉬토큰제거() throws Exception {
		//given
		String refreshToken = jwtService.createRefreshToken();
		jwtService.updateRefreshToken(username, refreshToken);
		clear();
		
		//when
		jwtService.destroyRefreshToken(username);
		clear();
		
		//then
		assertThrows(Exception.class, () -> ownerRepository.findByRefreshToken(refreshToken).get());
		
		Owner owner = ownerRepository.findById(username).get();
		assertThat(owner.getRefreshToken()).isNull();
	}
	
	@Test
	public void 액세스토큰헤더설정() throws Exception {
		//given
		MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
		String accessToken = jwtService.createAccessToken(username);
		String refreshToken = jwtService.createRefreshToken();
		
		jwtService.setAccessTokenHeader(mockHttpServletResponse, accessToken);
		
		//when
		jwtService.sendAccessAndRefreshToken(mockHttpServletResponse, accessToken, refreshToken);
		
		//then
		String headerAccessToken = mockHttpServletResponse.getHeader(accessHeader);
		assertThat(headerAccessToken).isEqualTo(accessToken);
	}
	
	@Test
	public void 리프레쉬토큰헤더설정() throws Exception {
		//given
		MockHttpServletResponse response = new MockHttpServletResponse();
		String accessToken = jwtService.createAccessToken(username);
		String refreshToken = jwtService.createRefreshToken();
		
		jwtService.setRefreshTokenHeader(response, refreshToken);
		
		//when
		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
		
		//then
		String headerRefreshToken = response.getHeader(refreshHeader);
		assertThat(headerRefreshToken).isEqualTo(refreshToken);
	}
	
	@Test
	public void 토큰전송() throws Exception {
		//given
		MockHttpServletResponse response = new MockHttpServletResponse();
		String accessToken = jwtService.createAccessToken(username);
		String refreshToken = jwtService.createRefreshToken();
		
		//when
		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
		
		//then
		String headerAccessToken = response.getHeader(accessHeader);
		String headerRefreshToken = response.getHeader(refreshHeader);
		
		assertThat(headerAccessToken).isEqualTo(accessToken);
		assertThat(headerRefreshToken).isEqualTo(refreshToken);
	}
	
	@Test
	public void 액세스토큰추출() throws Exception {
		//given
		String accessToken = jwtService.createAccessToken(username);
		String refreshToken = jwtService.createRefreshToken();
		HttpServletRequest request = setRequest(accessToken, refreshToken);
		
		//when
		String extractAccessToken = jwtService.extractAccessToken(request).get();
		
		//then
		assertThat(extractAccessToken).isEqualTo(accessToken);
		assertThat(getVerify(extractAccessToken).getClaim(USERNAME_CLAIM).asString()).isEqualTo(username);
	}
	
	@Test
	public void 리프레쉬토큰추출() throws Exception {
		//given
		String accessToken = jwtService.createAccessToken(username);
		String refreshToken = jwtService.createRefreshToken();
		HttpServletRequest request = setRequest(accessToken, refreshToken);
		
		//when
		String extractRefreshToken = jwtService.extractRefreshToken(request).get();
		
		//then
		assertThat(extractRefreshToken).isEqualTo(refreshToken);
		assertThat(getVerify(extractRefreshToken).getSubject()).isEqualTo(REFRESH_TOKEN_SUBJECT);
	}
	
	@Test
	public void username추출() throws Exception {
		//given
		String accessToken = jwtService.createAccessToken(username);
		String refreshToken = jwtService.createRefreshToken();
		HttpServletRequest request = setRequest(accessToken, refreshToken);
		
		String requestAccessToken = jwtService.extractAccessToken(request).get();
		
		//when
		String extractUsername = jwtService.extractId(requestAccessToken).get();
		
		//then
		assertThat(extractUsername).isEqualTo(username);
	}
	
	@Test
	public void 토큰유효성검사() throws Exception {
		//given
		String accessToken = jwtService.createAccessToken(username);
		String refreshToken = jwtService.createRefreshToken();
		
		//when
		//then
		assertThat(jwtService.isTokenValid(accessToken)).isTrue();
		assertThat(jwtService.isTokenValid(refreshToken)).isTrue();
		assertThat(jwtService.isTokenValid(accessToken + "d")).isFalse();
		assertThat(jwtService.isTokenValid(refreshToken + "d")).isFalse();
	}
}
