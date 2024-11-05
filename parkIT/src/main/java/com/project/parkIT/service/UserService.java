package com.project.parkIT.service;

import java.util.List;

import com.project.parkIT.domain.User;
import com.project.parkIT.domain.dto.UserDTO;

public interface UserService<T extends User> {
	String join(UserDTO dto);
	
	T findUser(String id);
	List<T> findAll();
	String findId(String tel, String name);
	String findPw(String id, String tel);
	
	T change(UserDTO dto);
	void remove(String id);
}
