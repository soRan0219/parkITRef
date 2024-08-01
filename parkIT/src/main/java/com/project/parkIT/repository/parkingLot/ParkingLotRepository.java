package com.project.parkIT.repository.parkingLot;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.parkIT.domain.ParkingLot;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, String>, ParkingLotRepositoryCustom {
	ParkingLot save(ParkingLot parkingLot);
	
	Optional<ParkingLot> findByCode(String code);
	List<ParkingLot> findAll();
	
	void deleteByCode(String code);
}
