package com.nb.banking.domain.account.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class TransferDto {

	@NotNull
	@Size(min = 14, max = 14)
	private String receiverAccountId;

	@NotNull
	private Long transferAmount;

}
