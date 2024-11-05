package com.project.parkIT.controller.owner;

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
	
	@GetMapping("/find")
	public ResponseEntity<String> find(@RequestParam(value="id") String id) {
		log.debug(" find() - id: {}", id);
		
		try {
			ownerService.validateDuplicateOwner(id);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미 존재하는 아이디입니다.");
		}
		
		return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 아이디입니다.");
	}
	
	@PostMapping("/join")
	public ResponseEntity<Owner> join(@RequestBody OwnerDTO dto) {
		Owner owner = ownerService.findUser(ownerService.join(dto));
		return ResponseEntity.status(HttpStatus.OK).body(owner);
	}
	
	@PostMapping(value = "/login")
	public ResponseEntity<Object> login(@RequestBody String id, @RequestBody String pw) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication.getPrincipal() instanceof UserDetailsImpl) {
			String ownerId = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
			Owner owner = ownerService.findUser(ownerId);
			return ResponseEntity.status(HttpStatus.OK).body(owner);
		}
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
	@GetMapping("/info")
	public ResponseEntity<Owner> info() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication.getPrincipal() instanceof UserDetailsImpl) {
			String ownerId = ((UserDetailsImpl) authentication.getPrincipal()).getUsername();
			log.debug(" info() - ownerId: " + ownerId);
			
			Owner owner = ownerService.findUser(ownerId);
			return ResponseEntity.status(HttpStatus.OK).body(owner);
		}
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}
	
}
