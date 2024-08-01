package com.project.parkIT.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkIT.domain.Member;
import com.project.parkIT.domain.dto.MemberDTO;
import com.project.parkIT.repository.member.MemberRepository;
import com.project.parkIT.service.member.MemberService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
public class MemberServiceTest {
	@Autowired MemberRepository memberRepository;
	@Autowired MemberService memberService;
	
	@Test
	public void 회원가입() {
		log.debug("** TEST: 회원가입");
		//given
		MemberDTO member = new MemberDTO();
		member.setId("id11");
		member.setPw("1234");
		member.setName("사용자1");
		member.setTel("010-1111-1111");
		
		//when
		String savedId = memberService.join(member);
		
		//then
		Member findedMember = memberService.findOne(savedId);
		
		assertThat(member.getName()).isEqualTo(findedMember.getName());
		log.debug("아이디: " + findedMember.getId());
		log.debug("비밀번호: " + findedMember.getPw());
		log.debug("이름: " + findedMember.getName());
	}
	
	@Test
	public void 중복가입예외() {
		log.debug("** TEST: 중복가입예외");
		//given
		MemberDTO member1 = new MemberDTO();
		member1.setId("ID1");
		member1.setPw("11");
		member1.setName("회원1");
		member1.setTel("010-1234-5678");
		
		MemberDTO member2 = new MemberDTO();
		member2.setId("ID1");
		member2.setPw("11");
		member2.setName("회원1");
		member2.setTel("010-1234-5678");
		
		//when
		memberService.join(member1);
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
		assertThat(e.getMessage()).isEqualTo("이미 존재하는 아이디입니다.");
//		assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
		
		//then
	}
	
	@Test
	public void 로그인() {
		log.debug("** TEST: 로그인");
		//given
		String id = "id1";
		String pw = "1111";
		
		//when
		Member member = memberService.findOne(id);
		String memberId = member.getId();
		String memberPw = member.getPw();
		
		//then
		if(id.equals(memberId) && pw.equals(memberPw)) log.debug("로그인 성공");
		else log.debug("로그인 실패");
	}
	
	@Test
	public void 아이디_찾기() {
		log.debug("** TEST: 아이디찾기");
		//given
		String tel = "010-2222-2222";
		String name = "이번";
		
		//when
		String memberId = memberService.findId(tel, name);
		
		//then
		log.debug("이름: " + name + "\n연락처: " + tel + "\n아이디: " + memberId);
	}
	
	@Test
	public void 비밀번호_찾기() {
		log.debug("** TEST: 비밀번호찾기");
		//given
		String id = "id2";
		String tel = "010-2222-2222";
		
		//when
		String memberPw = memberService.findPw(id, tel);
		
		//then
		log.debug("아이디: " + id + "\n연락처: " + tel + "\n비밀번호: " + memberPw);
		
	}
	
	@Test
	public void 회원정보_수정() {
		log.debug("** TEST: 회원정보수정");
		//given
		String updateEmail = "일번@one.one";
		
		//when
		MemberDTO member = new MemberDTO();
		member.setId("id1");
		member.setEmail(updateEmail);
		
		//then
		Member mem = memberService.update(member);
		log.debug("아이디: " + mem.getId());
		log.debug("이름: " + mem.getName());
		log.debug("이메일: " + mem.getEmail());
	}
	
	@Test
	public void 회원정보_삭제() {
		log.debug("** TEST: 회원정보삭제");
		//given
		String id = "id3";
//		String id = "id4";
//		MemberDTO memDto = new MemberDTO();
//		memDto.setId(id);
//		memberService.join(memDto);
		
		//when
		
		//then
		memberService.delete(id);
		
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.findOne(id));
		assertThat(e.getMessage()).isEqualTo("아이디와 비밀번호를 확인해주세요.");
//		assertThat(e.getMessage()).isEqualTo("아이디와 비밀번호를 확인해주세요.1");
		log.debug("회원 정보 삭제 완료");
	}
	
	@Test
	public void 예외() {
		log.debug("** TEST: 예외");
		//given
		String id = "id5";
		String tel = "010-1234-5678";
		String name = "이번";
		
		//when
//		IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.findOne(id));
//		IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.findId(tel, name));
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.findPw(id, tel));
		
		//then
//		assertThat(e.getMessage()).isEqualTo("아이디와 비밀번호를 확인해주세요.");
//		assertThat(e.getMessage()).isEqualTo("아이디와 비밀번호를 확인해주세요.1");
//		assertThat(e.getMessage()).isEqualTo("아이디를 찾을 수 없습니다.1");
		assertThat(e.getMessage()).isEqualTo("해당 아이디와 일치하는 비밀번호가 없습니다.1");
		
	}
}
