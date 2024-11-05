package com.project.parkIT.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.project.parkIT.domain.User;

@NoRepositoryBean
public interface UserRepository<T extends User> extends JpaRepository<T, String> {
	T save(T t);
	
	Optional<T> findById(String id);
	//아이디 찾을 때
	Optional<T> findByTelAndName(String tel, String name);
	//비번 찾을 때
	Optional<T> findByIdAndTel(String id, String tel);
	List<T> findAll();
	Optional<T> findByRefreshToken(String refreshToken);
	
	void deleteById(String id);
}
