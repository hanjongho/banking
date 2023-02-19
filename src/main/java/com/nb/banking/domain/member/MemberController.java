package com.nb.banking.domain.member;

import static com.nb.banking.global.config.ApiResult.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nb.banking.domain.member.dto.ConnectedMemberResponseDto;
import com.nb.banking.domain.member.dto.MemberRequestDto;
import com.nb.banking.domain.member.dto.MemberResponseDto;
import com.nb.banking.domain.member.entity.Member;
import com.nb.banking.global.config.ApiResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	// 회원가입 API
	@PostMapping("/join")
	public ApiResult<MemberResponseDto> join(@Valid @RequestBody MemberRequestDto memberRequestDto) {
		Member member = memberService.join(
				memberRequestDto.getLoginId(),
				memberRequestDto.getPassword(),
				memberRequestDto.getAmount());
		return OK(
				new MemberResponseDto(member.getId())
		);
	}

	// 내 친구 목록 조회 API
	// TODO id -> JwtAuthentication 객체의 id를 불러오는 과정으로 변경
	@GetMapping("/connections")
	public ApiResult<List<ConnectedMemberResponseDto>> connections(@RequestBody Map<String, String> inputMap) {
		return OK(
				memberService.findAllConnectedMember(inputMap.get("loginId")).stream()
						.map(ConnectedMemberResponseDto::new)
						.collect(Collectors.toList())
		);
	}

	// 친구 추가 API
	@PostMapping("/connections")
	// TODO id -> JwtAuthentication 객체의 id를 불러오는 과정으로 변경
	public void addConnection(@RequestBody Map<String, String> inputMap) {
		memberService.addConnection(inputMap.get("loginId"), inputMap.get("friendLoginId"));
	}
}
