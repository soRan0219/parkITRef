package com.project.parkIT.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.ParkingLot;
import com.project.parkIT.domain.ParkingLotDTO;
import com.project.parkIT.repository.ParkingLotRepository;

@Service
@Transactional
public class ParkingLotService {
	private final ParkingLotRepository parkingLotRepository;
	private final OwnerService ownerService;
	
	public ParkingLotService(ParkingLotRepository parkingLotRepository, OwnerService ownerService) {
		this.parkingLotRepository = parkingLotRepository;
		this.ownerService = ownerService;
	}
	
	//주차장 등록
	public String register(ParkingLotDTO dto) {
		Owner owner = ownerService.findOne(dto.getOwnerId());
		
		parkingLotRepository.findByCode(dto.getCode())
			.ifPresent(p -> {
				throw new IllegalStateException("이미 존재하는 코드입니다.");
			});
		
		ParkingLot parkingLot = ParkingLot.builder()
				.code(dto.getCode())
				.owner(owner)
				.name(dto.getName())
				.addr(dto.getAddr())
				.inOut(dto.getInOut())
				.total(dto.getTotal())
				.build();
		
		parkingLotRepository.save(parkingLot);
		return parkingLot.getCode();
	}
	
	
	//여러가지 조건(코드, 이름, 주소, 실내/외)으로 조회
	public List<ParkingLot> findParkingLots(ParkingLotDTO dto) {
		List<ParkingLot> list = parkingLotRepository.findByMultipleConditions(dto);
		
		if(list==null || list.isEmpty())
			throw new IllegalStateException("해당 조건과 일치하는 주차장이 존재하지 않습니다.");
		
		return list;
	}
	
	//주차장 정보 수정
	public ParkingLot update(ParkingLotDTO dto) {
		ParkingLot parkingLot = parkingLotRepository.findByCode(dto.getCode())
				.orElseThrow(() -> {
					throw new IllegalStateException("주차장 코드와 일치하는 주차장이 없습니다.");
				});
		
		parkingLot.updateParkingLot(dto);
		
		return parkingLot;
	}
	
	//주차장 삭제
	public void delete(String code) {
		parkingLotRepository.findByCode(code)
				.orElseThrow(() -> {
					throw new IllegalStateException("주차장 코드와 일치하는 주차장이 없습니다.");
				});
		
		parkingLotRepository.deleteByCode(code);
	}
	
}
