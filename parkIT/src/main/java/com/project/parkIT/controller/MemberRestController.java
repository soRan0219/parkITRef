package com.project.parkIT.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkIT.domain.Member;
import com.project.parkIT.domain.MemberDTO;
import com.project.parkIT.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberRestController {
	private final MemberService memberService;
	
	@PostMapping("/member/sign-up")
	public Member join(MemberDTO dto) {
		log.debug("id: {}, pw: {}", dto.getId(), dto.getPw());
		
		Member member = memberService.findOne(memberService.join(dto));
		
		return member;
	}
}
