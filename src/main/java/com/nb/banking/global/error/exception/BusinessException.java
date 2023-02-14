package com.nb.banking.global.error.exception;

import com.nb.banking.global.error.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
}
