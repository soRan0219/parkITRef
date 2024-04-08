package com.project.parkIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QParkingLot is a Querydsl query type for ParkingLot
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParkingLot extends EntityPathBase<ParkingLot> {

    private static final long serialVersionUID = 1252713708L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParkingLot parkingLot = new QParkingLot("parkingLot");

    public final StringPath addr = createString("addr");

    public final StringPath code = createString("code");

    public final StringPath inOut = createString("inOut");

    public final StringPath name = createString("name");

    public final QOwner owner;

    public final NumberPath<Integer> total = createNumber("total", Integer.class);

    public QParkingLot(String variable) {
        this(ParkingLot.class, forVariable(variable), INITS);
    }

    public QParkingLot(Path<? extends ParkingLot> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParkingLot(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParkingLot(PathMetadata metadata, PathInits inits) {
        this(ParkingLot.class, metadata, inits);
    }

    public QParkingLot(Class<? extends ParkingLot> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.owner = inits.isInitialized("owner") ? new QOwner(forProperty("owner")) : null;
    }

}

