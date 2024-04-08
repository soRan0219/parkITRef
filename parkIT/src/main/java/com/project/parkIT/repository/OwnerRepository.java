package com.project.parkIT.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.parkIT.domain.Owner;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, String> {
	Owner save(Owner owner);
	
	Optional<Owner> findById(String id);
	//아이디 찾기
	Optional<Owner> findByTelAndName(String tel, String name);
	//비밀번호 찾기
	Optional<Owner> findByIdAndTel(String id, String tel);
	List<Owner> findAll();
	
	void deleteById(String id);
}
