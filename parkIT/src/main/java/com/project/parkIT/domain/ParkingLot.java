package com.project.parkIT.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class ParkingLot {
	@Id
	@Column(name="pl_code", length=20, nullable=false)
	private String code;
	
	@ManyToOne
	@JoinColumn(name="owner_id")
	private Owner owner;
	
	@Column(name="pl_name", length=20, nullable=false)
	private String name;
	
	@Column(name="pl_addr", length=100, nullable=false)
	private String addr;
	
	@Column(name="pl_inout", length=10)
	private String inOut;
	
	@Column(name="pl_total", nullable=false)
	private int total;
	
	protected ParkingLot(String code, Owner owner, String name, String addr, int total) {
		this.code = code;
		
//		this.ownerId = ownerId;
		if(this.owner != null) this.owner.getPList().remove(this);
		this.owner = owner;
		owner.getPList().add(this);
		
		this.name = name;
		this.addr = addr;
		this.total = total;
	}
	
	public void updateParkingLot(ParkingLotDTO dto) {
		if(dto.getName()!=null && !dto.getName().equals(""))
			this.name = dto.getName();
		if(dto.getAddr()!=null && !dto.getAddr().equals(""))
			this.addr = dto.getAddr();
		if(dto.getInOut()!=null && !dto.getInOut().equals(""))
			this.inOut = dto.getInOut();
		if(dto.getTotal() >= 0)
			this.total = dto.getTotal();
	}
}
