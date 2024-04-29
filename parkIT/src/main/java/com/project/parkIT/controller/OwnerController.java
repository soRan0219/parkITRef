package com.project.parkIT.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.parkIT.service.OwnerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class OwnerController {
	private final OwnerService ownerService;
	
	public OwnerController(OwnerService ownerService) {
		this.ownerService = ownerService;
	}
	
	@GetMapping("/owner/sign-up")
	public String joinForm() {
//		log.debug("owner/join 이동");
		return "owner/joinForm";
	}
	
	@GetMapping("/owner/sign-in")
	public String loginForm() {
//		log.debug("owner/login 이동");
		return "owner/loginForm";
	}
	
}
