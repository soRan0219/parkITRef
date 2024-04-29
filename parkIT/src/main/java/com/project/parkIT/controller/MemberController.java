package com.project.parkIT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {
	
	@GetMapping("/member/sign-up")
	public String joinForm() {
		return "member/joinForm";
	}
	
	@GetMapping("/member/sign-in")
	public String loginForm() {
		return "member/loginForm";
	}
}
