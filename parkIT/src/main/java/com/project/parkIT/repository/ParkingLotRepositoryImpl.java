package com.project.parkIT.repository;

import static com.project.parkIT.domain.QParkingLot.parkingLot;

import java.util.List;

import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.ParkingLot;
import com.project.parkIT.domain.ParkingLotDTO;
import com.project.parkIT.domain.QOwner;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;;

@RequiredArgsConstructor
public class ParkingLotRepositoryImpl implements ParkingLotRepositoryCustom {
	private final JPAQueryFactory query;
	private QOwner qOwner = QOwner.owner;

	@Override
	public List<ParkingLot> findByMultipleConditions(ParkingLotDTO dto) {
		if(dto.getOwnerId()==null || dto.getOwnerId().isEmpty()) {
			dto.setOwnerId(" ");
		}
		
		Owner owner = query.selectFrom(qOwner)
				.where(qOwner.id.eq(dto.getOwnerId()))
				.fetchOne();
		
		return query.selectFrom(parkingLot)
				.where(eqOwner(owner), 
						eqCode(dto.getCode()),
						containsName(dto.getName()),
						containsAddr(dto.getAddr()),
						eqInOut(dto.getInOut()))
				.fetch();
	}
	
	private BooleanExpression eqOwner(Owner owner) {
		return owner==null ? null : parkingLot.owner.eq(owner);
	}
	
	private BooleanExpression eqCode(String code) {
		return StringUtils.isEmpty(code) ? null : parkingLot.code.eq(code);
	}

	private BooleanExpression containsName(String name) {
		return StringUtils.isEmpty(name) ? null : parkingLot.name.contains(name);
	}
	
	private BooleanExpression containsAddr(String addr) {
		return StringUtils.isEmpty(addr) ? null : parkingLot.addr.contains(addr);
	}
	
	private BooleanExpression eqInOut(String inOut) {
		return StringUtils.isEmpty(inOut) ? null : parkingLot.inOut.eq(inOut);
	}
	
}
