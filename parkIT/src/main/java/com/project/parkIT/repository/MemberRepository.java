package com.project.parkIT.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.parkIT.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
	Member save(Member member);
	
	Optional<Member> findById(String id);
	//아이디 찾기
	Optional<Member> findByTelAndName(String tel, String name);
	//비밀번호 찾기
	Optional<Member> findByIdAndTel(String id, String tel);
	List<Member> findAll();
	
	void deleteById(String id);
}
