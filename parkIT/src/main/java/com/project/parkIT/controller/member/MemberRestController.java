package com.project.parkIT.controller.member;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkIT.commons.security.UserDetailsImpl;
import com.project.parkIT.domain.Member;
import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.dto.MemberDTO;
import com.project.parkIT.service.member.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberRestController {
	private final MemberService memberService;
	
	@GetMapping("/find")
	public ResponseEntity<String> find(@RequestParam(value="id") String id) {
		log.debug(" find() - id: {}", id);
		
		try {
			memberService.validateDuplicateMember(id);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미 존재하는 아이디입니다.");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 아이디입니다.");
	}
	
	@PostMapping("/join")
	public ResponseEntity<Member> join(@RequestBody MemberDTO dto) {
		Member member = memberService.findUser(memberService.join(dto));
		return ResponseEntity.status(HttpStatus.OK).body(member);
	}
	
	@PostMapping(value = "/login")
	public ResponseEntity<Object> login(@RequestBody String id, @RequestBody String pw) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication.getPrincipal() instanceof UserDetailsImpl) {
			String memberId = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
			Member member = memberService.findUser(memberId);
			return ResponseEntity.status(HttpStatus.OK).body(member);
		}
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
	@GetMapping("/info")
	public ResponseEntity<Member> info() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication.getPrincipal() instanceof UserDetailsImpl) {
			String memberId = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
			log.debug(" info() - memberId: " + memberId);
			
			Member member = memberService.findUser(memberId);
			return ResponseEntity.status(HttpStatus.OK).body(member);
		}
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
}
