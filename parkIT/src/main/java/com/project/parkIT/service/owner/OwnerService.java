package com.project.parkIT.service.owner;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.dto.OwnerDTO;
import com.project.parkIT.domain.enums.Role;
import com.project.parkIT.repository.owner.OwnerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class OwnerService {
	private final OwnerRepository ownerRepository;
	private final PasswordEncoder encoder;
	
	//중복확인
	private void validateDuplicateOwner(String id) {
		ownerRepository.findById(id).
			ifPresent(m -> {
				throw new IllegalStateException("이미 존재하는 아이디입니다.");
			});
	}
	
	//회원가입
	public String join(OwnerDTO dto) {
		validateDuplicateOwner(dto.getId());
		dto.setPw(encoder.encode(dto.getPw()));
		
		Owner owner = Owner.builder()
				.id(dto.getId())
				.pw(dto.getPw())
				.name(dto.getName())
				.tel(dto.getTel())
				.email(dto.getEmail())
				.role(Role.OWNER)
				.build();
		
		ownerRepository.save(owner);
		
		return owner.getId();
	}
	
	//로그인
//	public Owner /*String*/ login(String id, String pw) {
//		Owner owner = ownerRepository.findById(id)
//				.orElseThrow(() -> {
//					throw new IllegalStateException("일치하는 아이디가 없습니다.");
//				});
//		
//		return owner.getRefreshToken();
		
//		return ownerRepository.findById(id)
//				.filter(it -> encoder.matches(pw, it.getPw()))
//				.orElseThrow(() -> {
//					throw new IllegalStateException("일치하는 아이디 또는 비밀번호가 없습니다.");
//				});
//	}
	
	//아이디로 조회
	public Owner findOne(String id) {
		return ownerRepository.findById(id)
				.orElseThrow(() -> {
					throw new IllegalStateException("일치하는 아이디가 없습니다.");
				});
	}
	
	//모든 점주 조회
	public List<Owner> findAll() {
		return ownerRepository.findAll();
	}
	
	//아이디 찾기
	public String findId(String tel, String name) {
		Owner owner = ownerRepository.findByTelAndName(tel, name)
				.orElseThrow(() -> {
					throw new IllegalStateException("아이디를 찾을 수 없습니다.");
				});
		
		return owner.getId();
	}
	
	//비밀번호 찾기
	public String findPw(String id, String tel) {
		Owner owner = ownerRepository.findByIdAndTel(id, tel)
				.orElseThrow(() -> {
					throw new IllegalStateException("비밀번호를 찾을 수 없습니다.");
				});
		
		return owner.getPw();
	}
	
	//점주 정보 수정
	public Owner update(OwnerDTO dto) {
		Owner owner = ownerRepository.findById(dto.getId())
				.orElseThrow(() -> {
					throw new IllegalStateException("점주를 찾을 수 없습니다.");
				});
		
		if(!encoder.matches(owner.getPw(), dto.getPw())) {
			dto.setPw(encoder.encode(dto.getPw()));
		}
		
		owner.updateOwner(dto);
		
		return owner;
	}
	
	//점주 정보 삭제
	public void delete(String id) {
		ownerRepository.findById(id)
				.orElseThrow(() -> {
					throw new IllegalStateException("점주를 찾을 수 없습니다.");
				});
		ownerRepository.deleteById(id);
	}
	
}
