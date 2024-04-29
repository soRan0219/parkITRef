package com.project.parkIT.domain;

import lombok.*;

@AllArgsConstructor
@Getter
public enum Role {
	MEMBER("ROLE_MEMBER"), 
	OWNER("ROLE_OWNER");
	
	private String roles;
}
