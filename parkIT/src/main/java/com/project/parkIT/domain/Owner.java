package com.project.parkIT.domain;

import java.util.*;

import com.project.parkIT.domain.dto.OwnerDTO;
import com.project.parkIT.domain.enums.Role;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Owner {
	
	@Id
	@Column(name="owner_id", length=15, nullable=false)
	private String id;
	
	@Column(name="owner_pw", length=2000, nullable=false)
	private String pw;
	
	@Column(name="owner_name", length=20, nullable=false)
	private String name;
	
	@Column(name="owner_tel", length=14, nullable=false, unique=true)
	private String tel;
	
	@Column(name="owner_email", length=50)
	private String email;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	//refreshToken : redis 적용 전
	@Column(name="refresh_token", length=500)
	private String refreshToken;
	
	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public void destroyRefreshToken() {
		this.refreshToken = null;
	}
	//refreshToken : redis 적용 전
	
	protected Owner(String id, String pw, String name, String tel, Role role) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.tel = tel;
		this.role = role;
	}
	
	@Builder.Default
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
	private List<ParkingLot> pList = new ArrayList<>();
	
	public void updateOwner(OwnerDTO dto) {
		if(dto.getPw()!=null && !dto.getPw().trim().isBlank()) 
			this.pw = dto.getPw();
		if(dto.getName()!=null && !dto.getName().trim().isBlank()) 
			this.name = dto.getName();
		if(dto.getTel()!=null && !dto.getTel().trim().isBlank()) 
			this.tel = dto.getTel();
		if(dto.getEmail()!=null && !dto.getEmail().trim().isBlank()) 
			this.email = dto.getEmail();
	}
}
