package com.nb.banking.domain.member.dto;

import com.nb.banking.domain.member.entity.ConnectedMember;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class ConnectedMemberResponseDto {

	private Long id;

	private String loginId;

	public ConnectedMemberResponseDto(ConnectedMember connectedMember) {
		this.id = connectedMember.getId();
		this.loginId = connectedMember.getLoginId();
	}
}
