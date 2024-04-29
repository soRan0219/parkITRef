package com.project.parkIT.domain;

import java.sql.Date;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Member {
	
	@Id
	@Column(name="member_id", length=15, nullable=false)
	private String id;
	
	@Column(name="member_pw", length=2000, nullable=false)
	private String pw;
	
	@Column(name="member_name", length=20, nullable=false)
	private String name;
	
	@Column(name="member_tel", length=13, nullable=false, unique=true)
	private String tel;
	
	@Column(name="member_birth")
	private Date birth;
	
	@Column(name="member_email", length=50)
	private String email;
	
	@Column(name="member_reg")
	private Date reg;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	protected Member(String id, String pw, String name, String tel, Date reg, Role role) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.tel = tel;
		this.reg = reg;
		this.role = role;
	}
	
	public void changeMember(MemberDTO dto) {
		if(dto.getPw()!=null && !dto.getPw().trim().isBlank()) 
			this.pw = dto.getPw();
		if(dto.getName()!=null && !dto.getName().trim().isBlank()) 
			this.name = dto.getName();
		if(dto.getTel()!=null && !dto.getTel().trim().isBlank()) 
			this.tel = dto.getTel();
		if(dto.getBirth()!=null) 
			this.birth = dto.getBirth();
		if(dto.getEmail()!=null && !dto.getEmail().trim().isBlank()) 
			this.email = dto.getEmail();
	}
}
