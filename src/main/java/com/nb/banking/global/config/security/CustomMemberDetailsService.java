package com.nb.banking.global.config.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nb.banking.domain.member.MemberRepository;
import com.nb.banking.domain.member.entity.Member;

@Component("userDetailsService")
public class CustomMemberDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	public CustomMemberDetailsService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String loginId) {
		return memberRepository.findOneWithAuthoritiesByLoginId(loginId)
				.map(member -> createUser(loginId, member))
				.orElseThrow(() -> new UsernameNotFoundException(loginId + " -> 데이터베이스에서 찾을 수 없습니다."));
	}

	private org.springframework.security.core.userdetails.User createUser(String loginId, Member member) {
		if (!member.isActivated()) {
			throw new RuntimeException(loginId + " -> 활성화되어 있지 않습니다.");
		}

		List<GrantedAuthority> grantedAuthorities = member.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
				.collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(member.getLoginId(),
				member.getPassword(),
				grantedAuthorities);
	}
}