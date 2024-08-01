package com.project.parkIT.domain.dto;

import lombok.*;

@Getter
@Setter
public class OwnerDTO {
	private String id;
	private String pw;
	private String pwConfirm;
	private String name;
	private String tel;
	private String email;
}
