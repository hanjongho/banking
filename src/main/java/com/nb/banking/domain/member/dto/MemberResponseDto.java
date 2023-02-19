package com.nb.banking.domain.member.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberResponseDto {
	private Long id;

	public MemberResponseDto(Long id) {
		this.id = id;
	}
}
