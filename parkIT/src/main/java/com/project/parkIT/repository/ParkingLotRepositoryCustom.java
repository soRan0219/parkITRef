package com.project.parkIT.repository;

import java.util.List;

import com.project.parkIT.domain.ParkingLot;
import com.project.parkIT.domain.ParkingLotDTO;

public interface ParkingLotRepositoryCustom {
	List<ParkingLot> findByMultipleConditions(ParkingLotDTO dto);
}
