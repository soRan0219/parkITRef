package com.project.parkIT.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.parkIT.commons.security.JwtService;
import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.enums.Role;
import com.project.parkIT.repository.owner.OwnerRepository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtFilterTest {

	@Autowired MockMvc mockMvc;
	@Autowired OwnerRepository ownerRepository;
	@Autowired EntityManager em;
	@Autowired JwtService jwtService;
	
	PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.access.header}")
	private String accessHeader;
	
	@Value("${jwt.refresh.header}")
	private String refreshHeader;
	
	private static String KEY_USERNAME = "id";
	private static String KEY_PASSWORD = "pw";
	private static String USERNAME = "username";
	private static String PASSWORD = "123456";
	
	private static String LOGIN_URL = "/login";
	
	private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
	private static final String BEARER = "Bearer";
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	private void clear() {
		em.flush();
		em.clear();
	}
	
	@BeforeEach
	private void init() {
		Owner owner = Owner.builder()
				.id(USERNAME)
				.pw(delegatingPasswordEncoder.encode(PASSWORD))
				.name("TestOwner")
				.email("owner@naver.com")
				.tel("000-0000-0000")
				.role(Role.OWNER)
				.build();
		
		ownerRepository.save(owner);
		clear();
	}
	
	private Map<String, String> getUsernamePasswordMap(String username, String password) {
		Map<String, String> map = new HashMap<>();
		map.put(KEY_USERNAME, username);
		map.put(KEY_PASSWORD, password);
		
		return map;
	}
	
	private Map<String, String> getAccessAndRefreshToken() throws Exception {
		Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);
		
		MvcResult result = mockMvc.perform(
					post(LOGIN_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(map))
				).andReturn();
		
		String accessToken = result.getResponse().getHeader(accessHeader);
		String refreshToken = result.getResponse().getHeader(refreshHeader);
		
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put(accessHeader, accessToken);
		tokenMap.put(refreshHeader, refreshToken);
		
		return tokenMap;
	}
	
	@Test
	public void 액세스_리프레쉬_모두없을때() throws Exception {
		//when
		//then
		mockMvc.perform(get(LOGIN_URL + "123")) // /login 외의 다른 주소
			//모두 없으므로 403
			.andExpect(status().isForbidden());
	}
	
	@Test
	public void 액세스만_있을때_유효() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		
		//when
		//then
		mockMvc.perform(get(LOGIN_URL + "123").header(accessHeader, BEARER + accessToken))
			//없는 주소로 보냈으므로 NotFound
			.andExpectAll(status().isNotFound());
	}
	
	@Test
	public void 액세스만_있을때_무효() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		
		//when
		//then
		mockMvc.perform(get(LOGIN_URL + "123").header(accessHeader, accessToken + "1"))
			//accessToken 유효하지 않으므로 403
			.andExpectAll(status().isForbidden());
	}
	
	@Test
	public void 리프레쉬만_있을때_유효() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
		
		//when
		//then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123").header(refreshHeader, BEARER + refreshToken))
				.andExpect(status().isOk()).andReturn();
		
		String accessToken = result.getResponse().getHeader(accessHeader);
		
		String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();
		assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
	}
	
	@Test
	public void 리프레쉬만_있을때_무효() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
		
		//when
		//then
		mockMvc.perform(get(LOGIN_URL + "123").header(refreshHeader, refreshToken)) //Bearer 안붙임
			//refreshToken 유효하지 않으므로 403
			.andExpect(status().isForbidden());
		
		mockMvc.perform(get(LOGIN_URL + "123").header(refreshHeader, BEARER + refreshToken + "1"))
			//refreshToken 유효하지 않으므로 403
			.andExpect(status().isForbidden());
	}
	
	@Test
	public void 액세스_리프레쉬_모두유효() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
		
		//when
		//then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
							.header(accessHeader, BEARER + accessToken)
							.header(refreshHeader, BEARER + refreshToken))
				.andExpect(status().isOk())
				.andReturn();
		
		String responseAccessToken = result.getResponse().getHeader(accessHeader);
		String responseRefreshToken = result.getResponse().getHeader(refreshHeader);
		
		String subject = JWT.require(Algorithm.HMAC512(secret)).build()
							.verify(responseAccessToken).getSubject();
		
		//accessToken만 재발급
		assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
		assertThat(responseRefreshToken).isNull();
	}
	
	@Test
	public void 액세스_무효_리프레쉬_유효() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
		
		//when
		//then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
							.header(accessHeader, BEARER + accessToken + "1")
							.header(refreshHeader, BEARER + refreshToken))
				.andExpect(status().isOk())
				.andReturn();
		
		String responseAccessToken = result.getResponse().getHeader(accessHeader);
		String responseRefreshToken = result.getResponse().getHeader(refreshHeader);
		
		String subject = JWT.require(Algorithm.HMAC512(secret)).build()
							.verify(responseAccessToken).getSubject();
		
		//accessToken만 재발급
		assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
		assertThat(responseRefreshToken).isNull();
	}
	
	@Test
	public void 액세스_유효_리프레쉬_무효() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
		
		//when
		//then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
							.header(accessHeader, BEARER + accessToken)
							.header(refreshHeader, BEARER + refreshToken + "1"))
				//없는 주소로 보냈으므로 NotFound
				.andExpect(status().isNotFound())
				.andReturn();
		
		String responseAccessToken = result.getResponse().getHeader(accessHeader);
		String responseRefreshToken = result.getResponse().getHeader(refreshHeader);
		
		//인증은 되나 둘 다 재발급하지 않음
		assertThat(responseAccessToken).isNull();
		assertThat(responseRefreshToken).isNull();
	}
	
	@Test
	public void 액세스_리프레쉬_모두무효() throws Exception {
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
		
		//when
		//then
		MvcResult result = mockMvc.perform(get(LOGIN_URL + "123")
				.header(accessHeader, BEARER + accessToken + "1")
				.header(refreshHeader, BEARER + refreshToken + "1"))
				//둘 다 유효하지 않으므로 403
				.andExpect(status().isForbidden())
				.andReturn();
		
		String responseAccessToken = result.getResponse().getHeader(accessHeader);
		String responseRefreshToken = result.getResponse().getHeader(refreshHeader);
		
		//둘 다 유효하지 않으므로 재발급하지 않음
		assertThat(responseAccessToken).isNull();
		assertThat(responseRefreshToken).isNull();
	}
	
	@Test
	public void 로그인주소_필터작동_안함() throws Exception {
		log.debug(" *** *** *** 로그인주소_필터작동_안함() *** *** *** ");
		//given
		Map accessAndRefreshToken = getAccessAndRefreshToken();
		String accessToken = (String) accessAndRefreshToken.get(accessHeader);
		String refreshToken = (String) accessAndRefreshToken.get(refreshHeader);
		
		//when
		//then
		MvcResult result = mockMvc.perform(get(LOGIN_URL)
							.header(accessHeader, BEARER + accessToken)
							.header(refreshHeader, BEARER + refreshToken))
				.andExpect(status().isNotFound())
				.andReturn();
		
		log.debug(" *** *** *** *** *** *** ");
	}
	
}
