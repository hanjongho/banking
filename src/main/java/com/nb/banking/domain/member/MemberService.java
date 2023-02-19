package com.nb.banking.domain.member;

import static com.google.common.base.Preconditions.*;
import static com.nb.banking.global.error.ErrorCode.*;
import static org.apache.logging.log4j.util.Strings.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nb.banking.domain.account.entity.Account;
import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.error.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	// private final PasswordEncoder passwordEncoder;

	@Transactional
	public Member join(String loginId, String password, Long amount) {
		checkArgument(isNotEmpty(password), "password must be provided");
		checkArgument(password.length() >= 4 && password.length() <= 15,
				"password length must be between 4 and 15 characters.");
		checkArgument(isNotEmpty(loginId), "loginId must be provided");

		// memberRepository.findByLoginId(loginId).orElseThrow(() -> new BadRequestException(MEMBER_ALREADY_EXIST));
		Optional<Member> byLoginId = memberRepository.findByLoginId(loginId);
		if (byLoginId.isPresent()) {
			throw new BadRequestException(MEMBER_ALREADY_EXIST);
		}

		Member newMember = new Member(loginId, password);
		newMember.setAccount(new Account(amount));

		memberRepository.save(newMember);

		return newMember;
	}

	public List<Member> findAllConnectedMember(String loginId) {
		checkArgument(loginId != null, "loginId must be provided");

		Member member = memberRepository.findByLoginId(loginId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));

		return member.getFriendList();
	}

	@Transactional
	public void addConnection(String loginId, String friendLoginId) {
		memberRepository.findByLoginId(loginId).orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));

		Member loginMember = memberRepository.findByLoginId(loginId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));
		Member friendMember = memberRepository.findByLoginId(friendLoginId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));

		// if (loginMember.getFriendList().contains(friendMember) || friendMember.getFriendList().contains(loginMember)) {
		// 	throw new BadRequestException(CONNECTION_ALREADY_EXIST);
		// }

		loginMember.addFriend(friendMember);
	}
}
