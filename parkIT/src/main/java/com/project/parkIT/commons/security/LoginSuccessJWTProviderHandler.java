package com.project.parkIT.commons.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkIT.repository.owner.OwnerRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
public class LoginSuccessJWTProviderHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtService jwtService;
	private final OwnerRepository ownerRepository;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		String id = extractId(authentication);
		String accessToken = jwtService.createAccessToken(id);
		String refreshToken = jwtService.createRefreshToken();
		
		jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
		ownerRepository.findById(id)
			.ifPresent(owner -> owner.updateRefreshToken(refreshToken));
		
		String refresh = ownerRepository.findById(id).get().getRefreshToken();
		log.info("refresh = " + refresh);
		
		log.info("로그인에 성공합니다. id: {}", id);
		log.info("AccessToken을 발급합니다. AccessToken: {}", accessToken);
		log.info("RefreshToken을 발급합니다. RefreshToken: {}", refreshToken);
		
		response.getWriter().write("success");
	}
	
	private String extractId(Authentication authentication) {
		OwnerDetails ownerDetails = (OwnerDetails) authentication.getPrincipal();
		return ownerDetails.getUsername();
	}
	
}
