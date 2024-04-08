package com.project.parkIT.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Owner {
	@Id
	@Column(name="owner_id", length=15)
	private String id;
	
	@Column(name="owner_pw", length=50)
	private String pw;
	
	@Column(name="owner_name", length=20)
	private String name;
	
	@Column(name="owner_tel", length=14)
	private String tel;
	
	@Column(name="owner_email", length=50)
	private String email;
	
	protected Owner(String id, String pw, String name, String tel) {
		this.id = id;
		this.pw = pw;
		this.name = name;
		this.tel = tel;
	}
	
	@Builder.Default
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
	private List<ParkingLot> pList = new ArrayList<>();
	
	public void updateOwner(OwnerDTO dto) {
		if(dto.getPw()!=null && !dto.getPw().equals("")) 
			this.pw = dto.getPw();
		if(dto.getName()!=null && !dto.getName().equals("")) 
			this.name = dto.getName();
		if(dto.getTel()!=null && !dto.getTel().equals("")) 
			this.tel = dto.getTel();
		if(dto.getEmail()!=null && !dto.getEmail().equals("")) 
			this.email = dto.getEmail();
	}
}
