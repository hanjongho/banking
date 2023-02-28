package com.nb.banking.domain.member.dto;

import com.nb.banking.domain.member.entity.Member;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ConnectedMemberResponseDto {

	private String loginId;

	public ConnectedMemberResponseDto(String loginId) {
		this.loginId = loginId;
	}
}
