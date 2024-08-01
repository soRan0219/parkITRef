package com.project.parkIT.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LogTestController {
	
	@GetMapping("/log-test")
	public void logTest() {
		log.trace("** TRACE **");
		log.debug("** DEBUG **");
		log.info("** INFO **");
		log.warn("** WARN **");
		log.error("** ERROR **");
	}
}
