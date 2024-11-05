package com.project.parkIT.commons.security;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.parkIT.domain.User;
import com.project.parkIT.domain.enums.Role;
import com.project.parkIT.repository.member.MemberRepository;
import com.project.parkIT.repository.owner.OwnerRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final OwnerRepository ownerRepository;
	private final MemberRepository memberRepository;
	
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
	
	// /join, /login으로 들어오는 요청은 이 필터 작동 x
	private final String[] NO_CHECK_URL = {"/join", "/login", "/find"};
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String uri = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/"));
		log.debug(" doFilterInternal() - uri: " + uri);
		
		
		// /join, /login으로 들어오는 요청은 이 필터 작동 x
		if(uri.equals(NO_CHECK_URL[0]) || uri.equals(NO_CHECK_URL[1]) || uri.equals(NO_CHECK_URL[2])) {
			log.debug(" doFilterInternal() - " + uri + "로 들어온 요청은 건너뜀");
			filterChain.doFilter(request, response);
			return;
		} 
		
		String accessToken = jwtService
								.extractAccessToken(request)
								.filter(jwtService::isTokenValid)
								.orElse(null);
		
		//accessToken 없거나 유효하지 않음
		if(accessToken == null) {
			log.debug(" doFilterInternal() - accessToken 없거나 유효x => refreshToken 검증 후 accessToken 재발급");
			checkRefreshTokenAndReIssueAccessToken(request, response);
			return;
		}
		//accessToken 유효 
		log.debug(" doFilterInternal() - accessToken 유효 => 인증");
		checkAccessTokenAndAuthenticate(request, response, filterChain, accessToken);

	}

	private void checkAccessTokenAndAuthenticate(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain, String accessToken) throws IOException, ServletException {
		log.debug(" checkAccessTokenAndAuthenticate() - id 추출 후 해당회원 인증");
		
		String role = jwtService.extractRole(accessToken)
				.orElseThrow(() -> new NoSuchElementException("Cannot find Role from this token."));
//		log.debug(" checkAccessTokenAndAuthenticate() - role : " + role);
		
		String id = jwtService.extractId(accessToken)
				.orElseThrow(() -> new NoSuchElementException("Cannot find id from this token."));
		
		if(role.contains(Role.OWNER.name())) {
			ownerRepository.findById(id).ifPresent(owner -> saveAuthentication(owner));
		} else if(role.contains(Role.MEMBER.name())) {
			memberRepository.findById(id).ifPresent(member -> saveAuthentication(member));
		}
		
		filterChain.doFilter(request, response);
		
	}

	private void saveAuthentication(User user) {
		log.debug(" saveAuthentication() - refreshToken 재발급 없이 인증");
		//회원정보로 인증처리
		UserDetailsImpl userDetails = new UserDetailsImpl(user);
		
		Authentication authentication= new UsernamePasswordAuthenticationToken(userDetails, null, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));
		
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private void checkRefreshTokenAndReIssueAccessToken(HttpServletRequest request, HttpServletResponse response/*, String refreshToken*/) throws IOException {
		log.debug("checkRefreshTokenAndReIssueAccessToken() - refreshToken에 해당하는 회원 찾아와서 accessToken 발급");
		
		String refreshToken = jwtService
								.extractRefreshToken(request)
								.filter(jwtService::isTokenValid)
								.orElse(null);
		
		log.info(" checkRefreshTokenAndReIssueAccessToken() - refreshToken: " + refreshToken);
		
		if(refreshToken == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("로그인 해주세요.");
			return;
		}
		
		String role = jwtService.extractRole(refreshToken)
				.orElseThrow(() -> new NoSuchElementException("Cannot find Role from this token."));
//		log.debug(" checkRefreshTokenAndReIssueAccessToken() - role : " + role);
		
		if(role.contains(Role.OWNER.name())) {
			ownerRepository.findByRefreshToken(refreshToken)
				.ifPresent(
						owner -> jwtService.sendAccessToken(response, jwtService.createAccessToken(owner))
					);
		} else if(role.contains(Role.MEMBER.name())) {
			memberRepository.findByRefreshToken(refreshToken)
				.ifPresent(
					member -> jwtService.sendAccessToken(response, jwtService.createAccessToken(member))
					);
		}
		
	}

}
