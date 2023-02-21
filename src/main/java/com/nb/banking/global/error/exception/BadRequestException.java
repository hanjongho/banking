package com.nb.banking.global.error.exception;

import com.nb.banking.global.error.ErrorCode;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BadRequestException extends BusinessException {
	private String message;

	public BadRequestException(String message) {
		super(ErrorCode._BAD_REQUEST);
		this.message = message;
	}

	public BadRequestException(ErrorCode errorCode, String message) {
		super(errorCode);
		this.message = message;
	}

	public BadRequestException(ErrorCode errorCode) {
		super(errorCode);
	}
}
