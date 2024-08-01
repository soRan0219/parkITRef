package com.project.parkIT.controller.owner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.parkIT.commons.security.OwnerDetails;
import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.dto.OwnerDTO;
import com.project.parkIT.service.owner.OwnerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/owner")
public class OwnerRestController {
	private final OwnerService ownerService;
	
	public OwnerRestController(OwnerService ownerService) {
		this.ownerService = ownerService;
	}
	
	@PostMapping("/join")
	public ResponseEntity<Owner> join(OwnerDTO dto) {
		log.debug("id: {}, pwd: {}", dto.getId(), dto.getPw());
		
		Owner owner = ownerService.findOne(ownerService.join(dto));
		
		return ResponseEntity.status(HttpStatus.OK).body(owner);
	}
	
	@PostMapping(value = "/login")
	public ResponseEntity<Object> login(@RequestBody String id, @RequestBody String pw) {
		log.debug("id: {}, pw: {}", id, pw);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication.getPrincipal() instanceof OwnerDetails) {
			String ownerId = ((OwnerDetails) authentication.getPrincipal()).getUsername();
			Owner owner = ownerService.findOne(ownerId);
			return ResponseEntity.status(HttpStatus.OK).body(owner);
		}
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
	@GetMapping("/info")
	public ResponseEntity<Owner> info() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication.getPrincipal() instanceof OwnerDetails) {
			String ownerId = ((OwnerDetails) authentication.getPrincipal()).getUsername();
			Owner owner = ownerService.findOne(ownerId);
			return ResponseEntity.status(HttpStatus.OK).body(owner);
		}
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
}
