package com.project.parkIT.commons.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.parkIT.domain.Owner;
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
	
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
	
	// /join, /login으로 들어오는 요청은 이 필터 작동 x
	private final String[] NO_CHECK_URL = {"/join", "/login"};
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String uri = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/"));
		
		// /join, /login으로 들어오는 요청은 이 필터 작동 x
		if(uri.equals(NO_CHECK_URL[0]) || uri.equals(NO_CHECK_URL[1])) {
			log.debug(" ** ** " + uri + "로 들어온 요청은 건너뜀");
			filterChain.doFilter(request, response);
			return;
		}
		
		String refreshToken = jwtService
								.extractRefreshToken(request)
								.filter(jwtService::isTokenValid)
								.orElse(null);
		
		//refreshToken 존재하는 경우
		if(refreshToken != null) {
			log.debug(" ** ** refreshToken 존재 => 유효한 경우 accessToken 재발급");
			checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
			return;
		}
		
		//refreshToken 없는 경우
		log.debug(" ** ** refreshToken 존재하지 않음 => accessToken 유효한지 확인");
		checkAccessTokenAndAuthentication(request, response, filterChain);
	}

	private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		//AccessToken 추출 후 해당 id로 회원 가져오고 인증
		//(refreshToken 재발급 x)
		jwtService.extractAccessToken(request).filter(jwtService::isTokenValid)
			.ifPresent(
					accessToken -> jwtService.extractId(accessToken)
						.ifPresent(
								id -> ownerRepository.findById(id)
								.ifPresent(
										owner -> saveAuthentication(owner)
										)
								)
						);
		
		filterChain.doFilter(request, response);
	}

	private void saveAuthentication(Owner owner) {
		log.debug(" ** ** accessToken 유효한 경우 재발급 없이 인증");
		//회원정보로 인증처리
		OwnerDetails ownerDetails = new OwnerDetails(owner);
		
		Authentication authentication= new UsernamePasswordAuthenticationToken(ownerDetails, null, authoritiesMapper.mapAuthorities(ownerDetails.getAuthorities()));
		
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
	}

	private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
		//refreshToken에 해당하는 회원 찾아와서 AccessToken 발급
		ownerRepository.findByRefreshToken(refreshToken)
			.ifPresent(
					owner -> jwtService.sendAccessToken(response, jwtService.createAccessToken(owner.getId()))
				);
	}

}
