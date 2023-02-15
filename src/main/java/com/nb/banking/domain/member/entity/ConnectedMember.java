package com.nb.banking.domain.member.entity;

import static com.google.common.base.Preconditions.*;

import lombok.Getter;

@Getter
public class ConnectedMember {

	private final Long id;

	private final String loginId;

	public ConnectedMember(Long id, String loginId) {
		checkArgument(id != null, "id must be provided");
		checkArgument(loginId != null, "loginId must be provided");

		this.id = id;
		this.loginId = loginId;
	}
}
