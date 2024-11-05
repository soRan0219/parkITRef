package com.project.parkIT.domain.dto;

public interface UserDTO {
	void setId(String id);
	void setPw(String pw);
	void setPwConfirm(String pwConfirm);
	void setName(String name);
	void setTel(String tel);
	void setEmail(String email);
	
	String getId();
	String getPw();
	String getPwConfirm();
	String getName();
	String getTel();
	String getEmail();
}
