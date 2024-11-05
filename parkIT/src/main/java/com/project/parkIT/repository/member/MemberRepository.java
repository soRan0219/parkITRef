package com.project.parkIT.repository.member;

import org.springframework.stereotype.Repository;

import com.project.parkIT.domain.Member;
import com.project.parkIT.repository.UserRepository;

@Repository
public interface MemberRepository extends UserRepository<Member> {
	
}
