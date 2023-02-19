package com.nb.banking.domain.member.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberRequestDto {

	private String loginId;

	private String password;

	private Long amount;

}
