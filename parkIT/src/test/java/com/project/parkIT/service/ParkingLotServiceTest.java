package com.project.parkIT.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.project.parkIT.domain.ParkingLot;
import com.project.parkIT.domain.dto.ParkingLotDTO;
import com.project.parkIT.repository.parkingLot.ParkingLotRepository;
import com.project.parkIT.service.parkingLot.ParkingLotService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
public class ParkingLotServiceTest {
	@Autowired ParkingLotRepository parkingLotRepo;
	@Autowired ParkingLotService parkingLotService;
	
	@Test
	public void 등록() {
		log.debug(" ** TEST: 주차장 등록");
		//given
		ParkingLotDTO dto = new ParkingLotDTO();
		dto.setCode("id3jkl78");
		dto.setOwnerId("id3");
		dto.setName("수원화성점");
		dto.setAddr("경기도 수원시 팔달구 장안동 1-1");
		dto.setInOut("실외");
		dto.setTotal(50);
		
		//when
		parkingLotService.register(dto);
		
		//then
//		ParkingLot parkingLot = parkingLotService.findCode(dto.getCode());
		ParkingLot parkingLot = parkingLotService.findParkingLots(dto).get(0);
		assertThat(parkingLot.getAddr()).isEqualTo(dto.getAddr());
		log.debug("코드: " + parkingLot.getCode() + "\n사장: " + parkingLot.getOwner().getId());
	}
	
	@Test
	public void 여러조건들() {
		log.debug(" ** TEST: 여러가지 조건으로 조회");
		//given
		ParkingLotDTO dto = new ParkingLotDTO();
		dto.setOwnerId("id2");
//		dto.setCode("id2def34");
//		dto.setName("공항");
//		dto.setAddr("부산광역시");
//		dto.setInOut("실외");
		
		//when
		List<ParkingLot> pList = parkingLotService.findParkingLots(dto);
		
		//then
		for(ParkingLot p : pList) {
//			assertThat(p.getInOut()).isEqualTo(inout);
			log.debug("사장: " + p.getOwner().getId());
			log.debug("코드: " + p.getCode());
			log.debug("지점명: " + p.getName());
			log.debug("주소: " + p.getAddr());
			log.debug("실내/외: " + p.getInOut());
		}
	}
	
	@Test
	public void 수정() {
		log.debug(" ** TEST: 정보 수정");
		//given
		ParkingLotDTO dto = new ParkingLotDTO();
		dto.setCode("id1abc12");
		dto.setTotal(125);
		
		//when
		ParkingLot lot = parkingLotService.update(dto);
		
		//then
		assertThat(dto.getCode()).isEqualTo(lot.getCode());
		log.debug("코드: " + lot.getCode());
		log.debug("수정 후 자리수: " + lot.getTotal());
	}
	
	@Test
	public void 삭제() {
		log.debug(" ** TEST: 정보 삭제");
		//given
		String code = "id2ghi56";
		
		//when
		parkingLotService.delete(code);
		
		//then
		ParkingLotDTO dto = new ParkingLotDTO();
		dto.setCode(code);
		
		IllegalStateException e = assertThrows(IllegalStateException.class 
				, () -> parkingLotService.findParkingLots(dto)
				, "예외가 발생하지 않았습니다.");
		assertThat(e.getMessage()).isEqualTo("주차장 코드와 일치하는 주차장이 없습니다.");
		log.debug("삭제 완료");
	}
	
	//예외
	@Test
	public void 코드중복예외() {
		log.debug(" ** TEST: 코드중복예외");
		//given
		ParkingLotDTO dto1 = new ParkingLotDTO();
		dto1.setCode("id1abc12");
		dto1.setOwnerId("id1");
		dto1.setName("송도신도시점");
		
		//when
		IllegalStateException e = assertThrows(IllegalStateException.class
				, () -> parkingLotService.register(dto1)
				, "예외가 발생하지 않았습니다.");
		
		//then
		assertThat(e.getMessage()).isEqualTo("이미 존재하는 코드입니다.");
	}
	
	@Test
	public void 조회예외() {
		log.debug(" ** TEST: 조회 예외");
		//given
		ParkingLotDTO dto = new ParkingLotDTO();
		dto.setOwnerId("id3");
		dto.setCode("id3abc12");
		dto.setName("해운대점");
		dto.setAddr("부산시 해운대구 반여동");
		dto.setInOut("시래");
		
		//when
		IllegalStateException e = assertThrows(IllegalStateException.class
				, () -> parkingLotService.findParkingLots(dto)
				, "예외가 발생하지 않았습니다.");
		
		//then
		assertThat(e.getMessage()).isEqualTo("해당 조건과 일치하는 주차장이 존재하지 않습니다.");
	}
	
}
