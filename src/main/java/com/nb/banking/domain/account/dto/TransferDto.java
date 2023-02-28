package com.nb.banking.domain.account.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class TransferDto {

	@NotNull
	private String receiverLoginId;

	@NotNull
	private Long transferAmount;

}
