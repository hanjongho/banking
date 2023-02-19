package com.nb.banking.domain.account.dto;

import lombok.Getter;

@Getter
public class TransferRequestDto {

	private String senderAccountId;

	private String recipientAccountId;

	private Long transferAmount;

}
