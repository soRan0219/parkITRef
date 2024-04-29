package com.project.parkIT.domain;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
	private String id;
	private String pw;
	private String pwConfirm;
	private String name;
	private String tel;
	private Date birth;
	private String email;
	private Date reg;
}
