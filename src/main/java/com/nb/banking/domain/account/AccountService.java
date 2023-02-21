package com.nb.banking.domain.account;

import static com.nb.banking.global.error.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nb.banking.domain.member.MemberRepository;
import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.error.exception.BadRequestException;
import com.nb.banking.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final MemberRepository memberRepository;

	@Transactional
	public void transfer(String senderId, String receiverId, Long transferAmount) {
		Member sender = memberRepository.findByWithPessimisticLock(senderId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));
		Member receiver = memberRepository.findByWithPessimisticLock(receiverId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));

		if (!(sender.getFriendList().contains(receiver) && receiver.getFriendList().contains(sender))) {
			throw new BusinessException(CONNECTION_NOT_EXIST);
		}

		if (sender.getAccount().getAmount() < transferAmount) {
			throw new BusinessException(ACCOUNT_INSUFFICIENT);
		}
		sender.getAccount().decreaseAmount(transferAmount);
		receiver.getAccount().increaseAmount(transferAmount);
		// TODO  알림 API -> Event 처리
	}
}
