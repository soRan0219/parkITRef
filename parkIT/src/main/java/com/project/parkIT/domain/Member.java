package com.project.parkIT.domain;

import java.sql.Date;

import com.project.parkIT.domain.dto.MemberDTO;
import com.project.parkIT.domain.dto.UserDTO;
import com.project.parkIT.domain.enums.Role;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Member implements User {
	
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
	
	//refreshToken : redis 적용 전
	@Column(name="refresh_token", length=500)
	private String refreshToken;
	
	public String getRole() {
		return role.getRoles();
	}
	
	@Override
	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	@Override
	public void destroyRefreshToken() {
		this.refreshToken = null;
	}
	//refreshToken : redis 적용 전
	
	protected Member(String id, String pw, String name, String tel, Date reg, Role role) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.tel = tel;
		this.reg = reg;
		this.role = role;
	}
	
	@Override
	public void update(UserDTO userDto) {
		MemberDTO dto = (MemberDTO) userDto;
		
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
