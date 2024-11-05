package com.project.parkIT.commons.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.parkIT.domain.Member;
import com.project.parkIT.domain.Owner;
import com.project.parkIT.domain.User;
import com.project.parkIT.repository.member.MemberRepository;
import com.project.parkIT.repository.owner.OwnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final OwnerRepository ownerRepository;
	private final MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String id = username.substring(username.indexOf("/")+1);
		
		if(username.startsWith("owner")) {
			Owner owner = ownerRepository.findById(id)
					.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
			return new UserDetailsImpl(owner);
		} else if(username.startsWith("member")) {
			Member member = memberRepository.findById(id)
					.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
			return new UserDetailsImpl(member);
		}
		
		return null;
	}

}
