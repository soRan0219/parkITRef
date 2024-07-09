package com.project.parkIT.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ReactTestController {

	@GetMapping("/api/test")
	public String hello() {
		log.debug(" *** React로부터 요청 도착 *** ");
		return "테스트입니다.";
	}
}
