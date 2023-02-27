package com.nb.banking.global.config;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.http.HttpStatus;

public class ApiError {

	private final String message;

	private final int status;

	private String code;

	ApiError(Throwable throwable, HttpStatus status, String code) {
		this(throwable.getMessage(), status, code);
	}

	ApiError(String message, HttpStatus status, String code) {
		this.message = message;
		this.status = status.value();
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public int getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("message", message)
				.append("status", status)
				.append("code", code)
				.toString();
	}

}