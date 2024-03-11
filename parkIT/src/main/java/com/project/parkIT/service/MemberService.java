package com.project.parkIT.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.project.parkIT.domain.Member;
import com.project.parkIT.domain.MemberDTO;
import com.project.parkIT.repository.MemberRepository;

@Transactional
public class MemberService {
	private final MemberRepository memberRepository;
	
	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	//중복확인
	private void validateDuplicateMember(String memberId) {
		memberRepository.findById(memberId)
		.ifPresent(m -> {
			throw new IllegalStateException("이미 존재하는 아이디입니다.");
		});
	}
	
	//회원가입
	public String join(MemberDTO memberDto) {
		validateDuplicateMember(memberDto.getId());
		Member member = Member.builder()
				.id(memberDto.getId())
				.pw(memberDto.getPw())
				.name(memberDto.getName())
				.tel(memberDto.getTel())
				.birth(memberDto.getBirth())
				.email(memberDto.getEmail())
				.reg(memberDto.getReg())
				.build();
		
		memberRepository.save(member);
		
		return member.getId();
	}
	
	//아이디로 조회 (로그인)
	public Member findOne(String memberId) {
		return memberRepository.findById(memberId).orElseThrow(() -> {
			throw new IllegalStateException("아이디와 비밀번호를 확인해주세요.");
		});
	}
	
	//모든 회원 조회
	public List<Member> findAll() {
		return memberRepository.findAll();
	}
	
	//아이디 찾기
	public String findId(String tel, String name) {
		Member member = memberRepository.findByTelAndName(tel, name)
			.orElseThrow(() -> {
				throw new IllegalStateException("아이디를 찾을 수 없습니다.");
			});
		
		return member.getId();
	}
	
	//비밀번호 찾기
	public String findPw(String id, String tel) {
		Member member = memberRepository.findByIdAndTel(id, tel)
				.orElseThrow(() -> {
					throw new IllegalStateException("해당 아이디와 일치하는 비밀번호가 없습니다.");
				});
		
		return member.getPw();
	}
	
	//회원정보 수정
	public Member update(MemberDTO updateMember) {
		Member member = memberRepository.findById(updateMember.getId())
				.orElseThrow(() -> {
					throw new IllegalStateException("회원을 찾을 수 없습니다.");
				});
		
		member.changeMember(updateMember);
		
		return member;
	}
	
	//회원 삭제
	public void delete(String id) {
		memberRepository.findById(id)
				.orElseThrow(() -> {
					throw new IllegalStateException("회원을 찾을 수 없습니다.");
				});
		memberRepository.deleteById(id);
	}
	
}
