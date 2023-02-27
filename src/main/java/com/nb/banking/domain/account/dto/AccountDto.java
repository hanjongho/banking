package com.nb.banking.domain.account.dto;

import com.nb.banking.domain.account.entity.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

	private String accountId;

	private Long amount;

	public static AccountDto from(Account account) {
		if (account == null)
			return null;

		return AccountDto.builder()
				.accountId(account.getAccountId())
				.amount(account.getAmount())
				.build();
	}
}