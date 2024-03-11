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
	@Column(name="member_id", length=15)
	private String id;
	
	@Column(name="member_pw", length=50)
	private String pw;
	
	@Column(name="member_name", length=20)
	private String name;
	
	@Column(name="member_tel", length=13)
	private String tel;
	
	@Column(name="member_birth")
	private Date birth;
	
	@Column(name="member_email", length=50)
	private String email;
	
	@Column(name="member_reg")
	private Date reg;
	
	protected Member(String id, String pw, String name, String tel, Date reg) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.tel = tel;
		this.reg = reg;
	}
	
	public void changeMember(MemberDTO dto) {
		if(dto.getPw()!=null && !dto.getPw().equals("")) 
			this.pw = dto.getPw();
		if(dto.getName()!=null && !dto.getName().equals("")) 
			this.name = dto.getName();
		if(dto.getTel()!=null && !dto.getTel().equals("")) 
			this.tel = dto.getTel();
		if(dto.getBirth()!=null) 
			this.birth = dto.getBirth();
		if(dto.getEmail()!=null && !dto.getEmail().equals("")) 
			this.email = dto.getEmail();
	}
}
