package com.nb.banking.domain.member;

import static com.google.common.base.Preconditions.*;
import static com.nb.banking.global.error.ErrorCode.*;
import static org.apache.logging.log4j.util.Strings.*;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.nb.banking.domain.member.entity.ConnectedMember;
import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.error.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	// private final PasswordEncoder passwordEncoder;

	public Member join(String loginId, String password) {
		checkArgument(isNotEmpty(password), "password must be provided");
		checkArgument(password.length() >= 4 && password.length() <= 15, "password length must be between 4 and 15 characters.");
		checkArgument(isNotEmpty(loginId), "loginId must be provided");

		Optional<Member> findMember = memberRepository.findByLoginId(loginId);
		if (findMember.isPresent()) {
			throw new BadRequestException(MEMBER_ALREADY_EXIST);
		}

		Member newMember = Member.builder()
			.loginId(loginId)
			.password(password)
			// .password(passwordEncoder.encode(password))
			.build();
		memberRepository.save(newMember);

		return newMember;
	}

	public List<ConnectedMember> findAllConnectedMember(String loginId) {
		checkArgument(loginId != null, "loginId must be provided");

		Optional<Member> findMember = memberRepository.findByLoginId(loginId);
		if (!findMember.isPresent()) {
			throw new BadRequestException(MEMBER_NOT_FOUND);
		}

		return memberRepository.findAllConnectedMemberByLoginId(loginId);
	}

	@Transactional
	public void addConnection(String loginId, String friendLoginId) {
		Member member1 = memberRepository.findByLoginId(loginId).orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));
		Member member2 = memberRepository.findByLoginId(friendLoginId).orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));

		member1.getFriendList().add(member2);
		member2.getFriendList().add(member1);
	}
}
