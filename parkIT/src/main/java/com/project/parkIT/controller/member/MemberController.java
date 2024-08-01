package com.project.parkIT.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {
	
	@GetMapping("/member/join")
	public String joinForm() {
		return "member/joinForm";
	}
	
	@GetMapping("/member/login")
	public String loginForm() {
		return "member/loginForm";
	}
}
