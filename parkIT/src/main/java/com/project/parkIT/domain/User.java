package com.project.parkIT.domain;

import com.project.parkIT.domain.dto.UserDTO;

public interface User {
	String getId();
	String getPw();
	String getName();
	String getTel();
	String getEmail();
	String getRole();
	
	String getRefreshToken();
	void updateRefreshToken(String refreshToken);
	void destroyRefreshToken();
	
	void update(UserDTO dto);
}
