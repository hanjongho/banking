package com.nb.banking.domain.member;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.nb.banking.domain.member.dto.MemberDto;
import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.error.exception.BadRequestException;

@DisplayName("회원 서비스")
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class MemberServiceTest {

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
	@DisplayName("회원가입")
	class join {

		@Test
		@DisplayName("성공")
		void success() {
			//given
			String loginId = "Jongho";
			String password = "1234";

			MemberDto memberDto = MemberDto.builder()
					.loginId(loginId)
					.password(password)
					.amount(1000L)
					.build();

			//when
			Member member = memberService.join(memberDto);

			//then
			assertNotNull(member.getAccount());
			assertEquals(member.getLoginId(), loginId);
		}

		@Test
		@DisplayName("중복")
		void duplication() {
			//given
			String loginId = "Jongho";
			String password = "1234";

			MemberDto memberDto = MemberDto.builder()
					.loginId(loginId)
					.password(password)
					.amount(1000L)
					.build();

			//when
			memberService.join(memberDto);

			//then
			assertThrows(BadRequestException.class, () -> memberService.join(memberDto));
		}

	}

	@Nested
	@DisplayName("내 친구 목록 조회")
	class findAllConnectedMember {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			//given
			String myId = "Jongho1";
			String myPassword = "1234";

			MemberDto memberDto = MemberDto.builder()
					.loginId(myId)
					.password(myPassword)
					.amount(1000L)
					.build();

			transaction.execute((status -> memberService.join(memberDto)));

			String friendId1 = "JonghoFriend1";
			String friendPassword1 = "5678";

			MemberDto friend1Dto = MemberDto.builder()
					.loginId(friendId1)
					.password(friendPassword1)
					.amount(1000L)
					.build();

			transaction.execute((status -> memberService.join(friend1Dto)));

			String friendId2 = "JonghoFriend2";
			String friendPassword2 = "8765";

			MemberDto friend2Dto = MemberDto.builder()
					.loginId(friendId2)
					.password(friendPassword2)
					.amount(1000L)
					.build();
			transaction.execute((status -> memberService.join(friend2Dto)));

			memberService.addConnection(myId, friendId1);
			memberService.addConnection(myId, friendId2);

			//when
			Set<Member> friendList = memberService.findAllConnectedMember(myId);

			//then
			Member me = memberRepository.findByLoginId(myId).get();
			Member friend1 = memberRepository.findByLoginId(friendId1).get();
			Member friend2 = memberRepository.findByLoginId(friendId2).get();
			assertAll(
					() -> assertEquals(2, friendList.size()),
					() -> assertTrue(me.getFriendList().contains(friend1)),
					() -> assertTrue(me.getFriendList().contains(friend1)),
					() -> assertTrue(friend1.getFriendList().contains(me)),
					() -> assertTrue(friend2.getFriendList().contains(me))
			);
		}

	}

	@Nested
	@DisplayName("친구 추가")
	class addConnection {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			//given
			String myId = "Jonghozzang";
			String myPassword = "1234";

			MemberDto memberDto = MemberDto.builder()
					.loginId(myId)
					.password(myPassword)
					.amount(1000L)
					.build();

			transaction.execute((status -> memberService.join(memberDto)));

			String friendId = "JonghoFriend5555";
			String friendPassword = "5678";

			MemberDto friendDto = MemberDto.builder()
					.loginId(friendId)
					.password(friendPassword)
					.amount(1000L)
					.build();

			transaction.execute((status -> memberService.join(friendDto)));

			//when
			memberService.addConnection(myId, friendId);

			//then
			Member me = memberRepository.findByLoginId(myId).get();
			Member you = memberRepository.findByLoginId(friendId).get();
			assertAll(
					() -> assertEquals(1, me.getFriendList().size()),
					() -> assertEquals(1, you.getFriendList().size()),
					() -> assertTrue(me.getFriendList().contains(you)),
					() -> assertTrue(you.getFriendList().contains(me))
			);
		}

		@Test
		@DisplayName("실패 - 이미 등록된 친구 일 때")
		void fail() throws Exception {
			//given
			String myId = "Jongho111111";
			String myPassword = "1234";

			MemberDto memberDto = MemberDto.builder()
					.loginId(myId)
					.password(myPassword)
					.amount(1000L)
					.build();
			transaction.execute((status -> memberService.join(memberDto)));

			String friendId = "JonghoFriend111111";
			String friendPassword = "5678";

			MemberDto friendDto = MemberDto.builder()
					.loginId(friendId)
					.password(friendPassword)
					.amount(1000L)
					.build();
			transaction.execute((status -> memberService.join(friendDto)));

			memberService.addConnection(myId, friendId);
			//when
			assertThrows(BadRequestException.class, () -> memberService.addConnection(myId, friendId));
		}

	}

}