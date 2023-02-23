package com.nb.banking.domain.member;

import static com.google.common.base.Preconditions.*;
import static com.nb.banking.global.error.ErrorCode.*;
import static org.apache.logging.log4j.util.Strings.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nb.banking.domain.account.entity.Account;
import com.nb.banking.domain.member.dto.MemberDto;
import com.nb.banking.domain.member.entity.Authority;
import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.config.security.util.SecurityUtil;
import com.nb.banking.global.error.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Member join(MemberDto memberDto) {
		String loginId = memberDto.getLoginId();
		String password = memberDto.getPassword();
		Long amount = memberDto.getAmount();

		checkArgument(isNotEmpty(password), "password must be provided");
		checkArgument(password.length() >= 4 && password.length() <= 15,
				"password length must be between 4 and 15 characters.");
		checkArgument(isNotEmpty(loginId), "loginId must be provided");

		memberRepository.findByLoginId(loginId).ifPresent((m -> {
			throw new BadRequestException(MEMBER_ALREADY_EXIST);
		}));

		Authority authority = Authority.builder()
				.authorityName("ROLE_MEMBER")
				.build();

		Member member = Member.builder()
				.loginId(loginId)
				.password(passwordEncoder.encode(password))
				.account(new Account(amount))
				.authorities(Collections.singleton(authority))
				.activated(true)
				.build();

		return memberRepository.save(member);
	}

	public Set<Member> findAllConnectedMember(String loginId) {
		// TODO Authentication에서 없으면 filter에서 걸러지지 않을까..
		checkArgument(loginId != null, "loginId must be provided");

		return memberRepository.findByLoginId(loginId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND)).getFriendList();
	}

	@Transactional
	public void addConnection(String loginId, String friendLoginId) {
		Member loginMember = memberRepository.findByLoginId(loginId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));
		Member friendMember = memberRepository.findByLoginId(friendLoginId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));

		if (isAlreadyFriend(loginMember, friendMember)) {
			throw new BadRequestException(CONNECTION_ALREADY_EXIST);
		}

		loginMember.addFriend(friendMember);
	}


	@Transactional(readOnly = true)
	public Optional<Member> getUserWithAuthorities(String username) {
		return memberRepository.findOneWithAuthoritiesByLoginId(username);
	}

	@Transactional(readOnly = true)
	public Optional<Member> getMyUserWithAuthorities() {
		return SecurityUtil.getCurrentUsername().flatMap(memberRepository::findOneWithAuthoritiesByLoginId);
	}

	private boolean isAlreadyFriend(Member loginMember, Member friendMember) {
		return loginMember.getFriendList().contains(friendMember) || friendMember.getFriendList().contains(loginMember);
	}
}
