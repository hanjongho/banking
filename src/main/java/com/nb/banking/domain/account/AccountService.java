package com.nb.banking.domain.account;

import static com.nb.banking.global.error.ErrorCode.*;

import java.util.concurrent.ConcurrentHashMap;

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
	public synchronized void transfer(String senderId, String receiverId, Long transferAmount) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		Member sender = memberRepository.findByLoginId(senderId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));
		Member receiver = memberRepository.findByLoginId(receiverId)
				.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));

		// TODO 친구 관계일 때만 송금가능
		// if (!(sender.getFriendList().contains(recipient) && recipient.getFriendList().contains(sender))) {
		// 	throw new BusinessException(CONNECTION_NOT_EXIST);
		// }

		// 다른 A -> B 입금 중 일 때 동시에 요청 중 C -> D 입금도 막아버림
		synchronized (this) {
			if (sender.getAccount().getAmount() < transferAmount) {
				throw new BusinessException(ACCOUNT_INSUFFICIENT);
			}
			sender.getAccount().decreaseAmount(transferAmount);
			receiver.getAccount().increaseAmount(transferAmount);
		}
		// 알림 API -> Event 처리
	}
}
