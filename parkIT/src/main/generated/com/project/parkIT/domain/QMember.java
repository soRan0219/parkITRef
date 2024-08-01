package com.project.parkIT.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 245785741L;

    public static final QMember member = new QMember("member1");

    public final DatePath<java.sql.Date> birth = createDate("birth", java.sql.Date.class);

    public final StringPath email = createString("email");

    public final StringPath id = createString("id");

    public final StringPath name = createString("name");

    public final StringPath pw = createString("pw");

    public final DatePath<java.sql.Date> reg = createDate("reg", java.sql.Date.class);

    public final EnumPath<com.project.parkIT.domain.enums.Role> role = createEnum("role", com.project.parkIT.domain.enums.Role.class);

    public final StringPath tel = createString("tel");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

