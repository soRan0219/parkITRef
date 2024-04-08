package com.project.parkIT.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.OwnerDTO;
import com.project.parkIT.repository.OwnerRepository;

@Service
@Transactional
public class OwnerService {
	private final OwnerRepository ownerRepository;
	
	public OwnerService(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}
	
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
		Owner owner = Owner.builder()
				.id(dto.getId())
				.pw(dto.getPw())
				.name(dto.getName())
				.tel(dto.getTel())
				.email(dto.getEmail())
				.build();
		
		ownerRepository.save(owner);
		
		return owner.getId();
	}
	
	//아이디 조회 (로그인)
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
