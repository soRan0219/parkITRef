package com.project.parkIT.commons.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.parkIT.domain.Owner;
import com.project.parkIT.repository.owner.OwnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerDetailsService implements UserDetailsService {

	private final OwnerRepository ownerRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Owner owner = ownerRepository.findById(username)
				.orElseThrow(() -> new IllegalArgumentException(username));		
		return new OwnerDetails(owner);
	}

}
