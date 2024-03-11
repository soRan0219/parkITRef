package com.project.parkIT.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.OwnerDTO;
import com.project.parkIT.repository.OwnerRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
public class OwnerServiceTest {
	@Autowired OwnerRepository ownerRepository;
	@Autowired OwnerService ownerService;
	
	@Test
	public void 회원가입() {
		log.debug(" ** TEST : 회원가입");
		//given
		OwnerDTO dto = new OwnerDTO();
		dto.setId("id4");
		dto.setPw("4444");
		dto.setName("사번");
		dto.setTel("010-4444-4444");
		
		//when
		String savedId = ownerService.join(dto);
		
		//then
		Owner owner = ownerService.findOne(savedId);
		assertThat(owner.getTel()).isEqualTo(dto.getTel());
		log.debug("아이디: " + owner.getId() + "\n 이름: " + owner.getName() + "\n전화번호: " + owner.getTel());
	}
	
	@Test
	public void 로그인() {
		log.debug(" ** TEST : 로그인");
		//given
		String id = "id2";
//		String pw = "2222";
		String pw = "2244";
		
		//when
		Owner owner = ownerService.findOne(id);
		String ownerId = owner.getId();
		String ownerPw = owner.getPw();
		
		//then
		if(id.equals(ownerId) && pw.equals(ownerPw)) log.debug("로그인 성공");
		else log.debug("로그인 실패");
	}
	
	@Test
	public void 아이디찾기() {
		log.debug(" ** TEST : 아이디찾기");
		//given
		String name = "삼번";
		String tel = "010-3333-3333";
		
		//when
		String findId = ownerService.findId(tel, name);
		
		//then
		Owner owner = ownerService.findOne(findId);
		log.debug("이름: " + owner.getName() + "\n전화번호: " + owner.getTel() + "\n아이디: " + owner.getId());
	}
	
	@Test
	public void 비번찾기() {
		log.debug(" ** TEST : 비번찾기");
		//given
		String id = "id1";
		String tel = "010-1111-1111";
		
		//when
		String findPw = ownerService.findPw(id, tel);
		
		//then
		log.debug("아이디: " + id + "\n전화번호: " + tel + "\n비밀번호: " + findPw);
	}
	
	@Test
	public void 점주수정() {
		log.debug(" ** TEST : 점주수정");
		//given
		OwnerDTO dto = new OwnerDTO();
		dto.setId("id3");
		dto.setPw("3366");
		dto.setEmail("three@삼.삼");
		
		//when
		Owner owner = ownerService.update(dto);
		
		//then
		log.debug("아이디: " + owner.getId());
		log.debug("수정된 비번: " + owner.getPw());
		log.debug("수정된 이메일: " + owner.getEmail());
		log.debug("전화번호: " + owner.getTel());
	}
	
	@Test
	public void 점주삭제() {
		log.debug(" ** TEST : 점주삭제");
		//given
		String id = "id3";
		
		//when
		ownerService.delete(id);
		
		//then
		IllegalStateException e = assertThrows(IllegalStateException.class
				, () -> ownerService.findOne(id)
				, "예외가 발생하지 않았습니다.");
		assertThat(e.getMessage()).isEqualTo("일치하는 아이디가 없습니다.");
		log.debug("삭제 완료");
	}
	
	@Test
	public void 중복가입예외() {
		log.debug(" ** TEST : 중복가입예외");
		//given
		OwnerDTO dto1 = new OwnerDTO();
		dto1.setId("id4");
		dto1.setPw("4444");
		dto1.setName("사번");
		dto1.setTel("010-4444-4444");
		dto1.setEmail("사번@four.four");
		
		OwnerDTO dto2 = new OwnerDTO();
		dto2.setId("id4");
//		dto2.setId("id5");
		dto2.setPw("44");
		dto2.setName("4번");
		dto2.setTel("010-4444-5678");
		dto2.setEmail("사번@four.4");
		
		//when
		ownerService.join(dto1);
		
		//then
		IllegalStateException e = assertThrows(IllegalStateException.class
				, () -> ownerService.join(dto2)
				, "예외가 발생하지 않았습니다.");
		assertThat(e.getMessage()).isEqualTo("이미 존재하는 아이디입니다.");
	}
	
	@Test
	public void 아이디조회예외() {
		log.debug(" ** TEST : 아이디조회예외");
		//given
		String id = "id5";
//		String id = "id2";
		
		//when
		IllegalStateException e = assertThrows(IllegalStateException.class
				, () -> ownerService.findOne(id)
				, "예외가 발생하지 않았습니다.");
		
		//then
		assertThat(e.getMessage()).isEqualTo("일치하는 아이디가 없습니다.");
	}
	
	@Test
	public void 아이디찾기예외() {
		log.debug(" ** TEST : 아이디찾기예외");
		//given
		String tel = "010-1111-1111";
		String name = "이번";
//		String name = "일번";
		
		//when
		IllegalStateException e = assertThrows(IllegalStateException.class
				, () -> ownerService.findId(tel, name)
				, "예외가 발생하지 않았습니다.");
		
		//then
		assertThat(e.getMessage()).isEqualTo("아이디를 찾을 수 없습니다.");
	}
	
	@Test
	public void 비번찾기예외() {
		log.debug(" ** TEST : 비번찾기예외");
		//given
		String id = "id2";
//		String id = "id1";
		String tel = "010-1111-1111";
		
		//when
		IllegalStateException e = assertThrows(IllegalStateException.class
				, () -> ownerService.findPw(id, tel)
				, "예외가 발생하지 않았습니다.");
		
		//then
		assertThat(e.getMessage()).isEqualTo("비밀번호를 찾을 수 없습니다.");
	}
	
}
