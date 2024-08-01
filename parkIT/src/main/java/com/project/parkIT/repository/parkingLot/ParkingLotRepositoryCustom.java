package com.project.parkIT.repository.parkingLot;

import java.util.List;

import com.project.parkIT.domain.ParkingLot;
import com.project.parkIT.domain.dto.ParkingLotDTO;

public interface ParkingLotRepositoryCustom {
	List<ParkingLot> findByMultipleConditions(ParkingLotDTO dto);
}
