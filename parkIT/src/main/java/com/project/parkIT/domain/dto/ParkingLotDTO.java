package com.project.parkIT.domain.dto;

import lombok.*;

@Getter
@Setter
public class ParkingLotDTO {
	private String code;
	private String ownerId;
	private String name;
	private String addr;
	private String inOut;
	private int total;
}
