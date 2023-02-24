package com.nb.banking.domain.member;

import static com.nb.banking.global.config.ApiResult.*;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nb.banking.domain.member.dto.ConnectedMemberResponseDto;
import com.nb.banking.domain.member.dto.LoginDto;
import com.nb.banking.domain.member.dto.MemberDto;
import com.nb.banking.domain.member.dto.TokenDto;
import com.nb.banking.global.config.ApiResult;
import com.nb.banking.global.config.security.jwt.JwtFilter;
import com.nb.banking.global.config.security.jwt.TokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/member")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	// 회원가입 API
	@PostMapping("/join")
	public ApiResult<MemberDto> join(@Valid @RequestBody MemberDto memberDto) {
		return OK(MemberDto.from(memberService.join(memberDto)));
	}

	// 내 친구 목록 조회 API
	// TODO id -> JwtAuthentication 객체의 id를 불러오는 과정으로 변경
	@GetMapping("/connections")
	@PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
	public ApiResult<List<ConnectedMemberResponseDto>> connections(Authentication authentication) {
		return OK(memberService.findAllConnectedMember(authentication.getName())
				.stream()
				.map(ConnectedMemberResponseDto::new)
				.collect(Collectors.toList()));
	}

	// 친구 추가 API
	@PostMapping("/connections/{friendId}")
	@PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
	public void addConnection(Authentication authentication, String friendId) {
		memberService.addConnection(authentication.getName(), friendId);
	}

	@PostMapping("/authenticate")
	public ApiResult<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginDto.getLoginId(), loginDto.getPassword());

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.createToken(authentication);
		response.addHeader(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
		return OK(new TokenDto(jwt));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('MEMBER','ADMIN')")
	public ApiResult<MemberDto> getMyUserInfo() {
		return OK(MemberDto.from(memberService.getMyUserWithAuthorities().get()));
	}

	@GetMapping("/{loginId}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ApiResult<MemberDto> getUserInfo(@PathVariable String loginId) {
		return OK(MemberDto.from(memberService.getUserWithAuthorities(loginId).get()));
	}
}
