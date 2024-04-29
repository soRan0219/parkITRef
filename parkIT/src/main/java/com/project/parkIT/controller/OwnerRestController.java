package com.project.parkIT.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.OwnerDTO;
import com.project.parkIT.service.OwnerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class OwnerRestController {
	private final OwnerService ownerService;
	
	public OwnerRestController(OwnerService ownerService) {
		this.ownerService = ownerService;
	}
	
	@PostMapping("/owner/sign-up")
	public Owner join(OwnerDTO dto) {
		log.debug("id: {}, pwd: {}", dto.getId(), dto.getPw());
		
		Owner owner = ownerService.findOne(ownerService.join(dto));
		
		return owner;
	}
	
	@PostMapping("/owner/sign-in")
	public Owner login(@RequestParam("id") String id, @RequestParam("pwd") String pwd) {
		log.debug("id: {}, pwd: {}", id, pwd);
		
//		Owner owner = ownerService.findOne(id);
		Owner owner = ownerService.login(id, pwd);
		
//		if(owner.getId().equals(id) && owner.getPw().equals(pwd)) {
//			log.debug("로그인 성공");
			return owner;
//		} else {
//			log.debug("로그인 실패");
//			return "";
//		}
		
	}
}
