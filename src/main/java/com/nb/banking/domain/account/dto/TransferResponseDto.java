package com.nb.banking.domain.account.dto;

import lombok.Getter;

@Getter
public class TransferResponseDto {

	private String senderAccountId;

	private String recipientAccountId;
}
