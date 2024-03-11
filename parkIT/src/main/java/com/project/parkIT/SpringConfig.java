package com.project.parkIT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.parkIT.repository.MemberRepository;
import com.project.parkIT.repository.OwnerRepository;
import com.project.parkIT.service.MemberService;
import com.project.parkIT.service.OwnerService;

@Configuration
public class SpringConfig {
	private final MemberRepository memberRepository;
	private final OwnerRepository ownerRepository;
	
	@Autowired
	public SpringConfig(MemberRepository memberRepository, OwnerRepository ownerRepository) {
		this.memberRepository = memberRepository;
		this.ownerRepository = ownerRepository;
	}
	
	@Bean
	public MemberService memberService() {
		return new MemberService(memberRepository);
	}
	@Bean
	public OwnerService ownerService() {
		return new OwnerService(ownerRepository);
	}
}
