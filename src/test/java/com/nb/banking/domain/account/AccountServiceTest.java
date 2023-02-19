package com.nb.banking.domain.account;

import static com.nb.banking.global.error.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.nb.banking.domain.member.MemberRepository;
import com.nb.banking.domain.member.MemberService;
import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.error.exception.BadRequestException;
import com.nb.banking.global.error.exception.BusinessException;

@DisplayName("계좌 서비스")
@ActiveProfiles("test")
@SpringBootTest
// @Transactional
class AccountServiceTest {

	@Autowired
	private AccountService accountService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberRepository memberRepository;

	private TransactionTemplate transaction;

	@Autowired
	private PlatformTransactionManager tm;

	@BeforeEach
	void setUp() {
		transaction = new TransactionTemplate(tm);
		transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
	}

	@Nested
	@DisplayName("계좌 이체")
	class transfer {

		@Test
		@DisplayName("성공")
		@Rollback
		void success() throws Exception {
			//given
			String senderId = "sender1";
			String senderPw = "1234";
			memberService.join(senderId, senderPw, 100000L);

			String receiverId = "receiver1";
			String receiverPw = "5678";
			memberService.join(receiverId, receiverPw, 0L);

			memberService.addConnection(senderId, receiverId);

			//when
			accountService.transfer(senderId, receiverId, 30000L);

			//then
			Member sender = memberRepository.findByLoginId(senderId)
					.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));
			Member receiver = memberRepository.findByLoginId(receiverId)
					.orElseThrow(() -> new BadRequestException(MEMBER_NOT_FOUND));
			assertEquals(70000L, sender.getAccount().getAmount());
			assertEquals(30000L, receiver.getAccount().getAmount());
		}

		// 친구 목록 연결 재 구현
		@Test
		@Disabled
		@DisplayName("실패 - 친구 연결 안되어 있을 때")
		void fail_no_connection() throws Exception {
			//given
			String senderId = "Jongho";
			String senderPw = "12345678";
			memberService.join(senderId, senderPw, 100000L);

			String receiverId = "Han";
			String receiverPw = "0000";
			memberService.join(receiverId, receiverPw, 0L);

			//when
			assertThrows(BusinessException.class, () -> accountService.transfer(senderId, receiverId, 30000L));
		}

		@Test
		@DisplayName("성공 - 200개 스레드에서 동시에 입금")
		void success_total_200_threads() throws Exception {
			//given
			String receiverId = "recipient";
			String receiverPw = "1234";
			transaction.execute((status -> memberService.join(receiverId, receiverPw, 0L)));
			String senderId = UUID.randomUUID().toString();
			String senderPw = "12345678";
			transaction.execute((status -> memberService.join(senderId, senderPw, 1000L)));

			// memberService.addConnection(receiverId, senderId);

			//when
			int numberOfThreads = 200;
			ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
			CountDownLatch latch = new CountDownLatch(numberOfThreads);
			for (int i = 0; i < numberOfThreads; i++) {
				service.execute(() -> {
					transaction.execute((status -> {
							accountService.transfer(senderId, receiverId, 1L);
						latch.countDown();
						return null;
					}));
				});
			}
			latch.await();
			Thread.sleep(1000);
			//then

			Member sender = memberRepository.findByLoginId(senderId).get();
			Member receiver = memberRepository.findByLoginId(receiverId).get();

			// assertEquals(1, sender.getVersion());
			// assertEquals(1, receiver.getVersion());
			assertEquals(1000L - numberOfThreads, sender.getAccount().getAmount());
			assertEquals(0 + numberOfThreads, receiver.getAccount().getAmount());
		}
	}

}