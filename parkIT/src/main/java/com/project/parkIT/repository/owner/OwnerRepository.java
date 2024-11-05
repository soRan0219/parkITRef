package com.project.parkIT.repository.owner;

import org.springframework.stereotype.Repository;

import com.project.parkIT.domain.Owner;
import com.project.parkIT.repository.UserRepository;

@Repository
public interface OwnerRepository extends UserRepository<Owner> {
	
}
