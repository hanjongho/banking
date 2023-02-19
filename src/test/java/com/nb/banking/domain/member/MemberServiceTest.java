package com.nb.banking.domain.member;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.error.exception.BadRequestException;

@DisplayName("회원 서비스")
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class MemberServiceTest {

	@Autowired
	private MemberService memberService;

	@Nested
	@DisplayName("회원가입")
	class join {

		@Test
		@DisplayName("성공")
		void success() {
			//given
			String loginId = "Jongho";
			String password = "1234";

			//when
			Member member = memberService.join(loginId, password, 0L);

			//then
			assertNotNull(member.getAccount());
			assertEquals(member.getLoginId(), loginId);
			assertEquals(member.getPassword(), password);
		}

		@Test
		@DisplayName("중복")
		void duplication() {
			//given
			String loginId = "Jongho";
			String password = "1234";

			//when
			memberService.join(loginId, password, 0L);

			//then
			assertThrows(BadRequestException.class, () -> memberService.join(loginId, password, 0L));
		}

	}

	@Nested
	@DisplayName("내 친구 목록 조회")
	class findAllConnectedMember {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			//given
			String myId = "Jongho";
			String myPassword = "1234";
			memberService.join(myId, myPassword, 0L);

			String friendId1 = "JonghoFriend1";
			String friendPassword1 = "5678";
			Member friend1 = memberService.join(friendId1, friendPassword1, 0L);

			String friendId2 = "JonghoFriend2";
			String friendPassword2 = "8765";
			Member friend2 = memberService.join(friendId2, friendPassword2, 0L);

			memberService.addConnection(myId, friendId1);
			memberService.addConnection(myId, friendId2);

			//when
			List<Member> friendList = memberService.findAllConnectedMember(myId);

			//then
			assertEquals(2, friendList.size());
			assertEquals(myId, friend1.getFriendList().get(0).getLoginId());
			assertEquals(myId, friend2.getFriendList().get(0).getLoginId());
		}

	}

	@Nested
	@DisplayName("친구 추가")
	class addConnection {

		@Test
		@DisplayName("성공")
		void success() throws Exception {
			//given
			String myId = "Jongho";
			String myPassword = "1234";
			Member me = memberService.join(myId, myPassword, 0L);

			String friendId = "JonghoFriend";
			String friendPassword = "5678";
			Member you = memberService.join(friendId, friendPassword, 0L);

			memberService.addConnection(myId, friendId);

			//when
			List<Member> friendList = memberService.findAllConnectedMember(myId);

			//then
			assertEquals(friendList.size(), 1);
			assertEquals(friendList.get(0).getLoginId(), friendId);
			assertTrue(you.getFriendList().contains(me));
			assertTrue(me.getFriendList().contains(you));
		}

		@Test
		@DisplayName("실패 - 이미 등록된 친구 일 때")
		void fail() throws Exception {
			//given
			String myId = "Jongho";
			String myPassword = "1234";
			Member me = memberService.join(myId, myPassword, 0L);

			String friendId = "JonghoFriend";
			String friendPassword = "5678";
			Member you = memberService.join(friendId, friendPassword, 0L);

			memberService.addConnection(myId, friendId);

			//when
			assertThrows(BadRequestException.class, () -> memberService.addConnection(myId, friendId));
		}

	}

}