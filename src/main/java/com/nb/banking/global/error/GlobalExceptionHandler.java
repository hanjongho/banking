package com.nb.banking.global.error;

import static com.nb.banking.global.config.ApiResult.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import com.nb.banking.global.config.ApiResult;
import com.nb.banking.global.error.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<ApiResult<?>> newResponse(String message, HttpStatus status, String code) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		return new ResponseEntity<>(ERROR(message, status, code), headers, status);
	}

	/**
	 *  비즈니스 로직 익셉션 처리하는 핸들러
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleBusinessException(BusinessException e) {
		log.error("BusinessException", e);

		final ErrorCode errorCode = e.getErrorCode();

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}

	/**
	 *  javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
	 *  HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
	 *  주로 @RequestBody, @RequestPart 어노테이션에서 발생
	 */
	@ExceptionHandler
	protected ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error("handleMethodArgumentNotValidException", e);

		final ErrorCode errorCode = ErrorCode.PARAMETER_NOT_VALID;

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}

	/**
	 * enum type 일치하지 않아 binding 못할 경우 발생
	 * 주로 @RequestParam enum으로 binding 못했을 경우 발생
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		log.error("handleMethodArgumentTypeMismatchException", e);

		ErrorCode errorCode = ErrorCode._INVALID_REQUEST_PARAMETER;

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}

	/**
	 * Request Param 타입이 일치하지 않을 때 발생
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		log.error("handleMissingServletRequestParameterException", e);

		ErrorCode errorCode = ErrorCode._INVALID_REQUEST_PARAMETER;

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}

	/**
	 * 지원하지 않은 HTTP method 호출 할 경우 발생
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		log.error("handleHttpRequestMethodNotSupportedException", e);

		ErrorCode errorCode = ErrorCode._METHOD_NOT_ALLOWED;

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}

	/**
	 * 지원하지 않은 HTTP Media PendingType 호출 할 경우 발생
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	protected ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
		log.error("handleHttpMediaTypeNotSupportedException", e);

		ErrorCode errorCode = ErrorCode._UNSUPPORTED_MEDIA_TYPE;

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}

	/**
	 * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생합
	 */
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
		log.error("handleAccessDeniedException", e);

		ErrorCode errorCode = ErrorCode._UNAUTHORIZED;

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}

	/**
	 * 로그인 정보가 일치하지 않을 때
	 */
	@ExceptionHandler({BadCredentialsException.class})
	protected ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
		log.error("handleBadCredentialsException", e);

		ErrorCode errorCode = ErrorCode.LOGIN_FAILED;

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}

	/**
	 * 파일 업로드 시 멀티파트 헤더를 설정하지 않았을때 에러
	 */
	@ExceptionHandler({MultipartException.class})
	protected ResponseEntity<?> handleMultipartException(MultipartException e) {
		log.error("handleMultipartException", e);

		ErrorCode errorCode = ErrorCode.NOT_MULTIPART_HEADER;

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}

	@ExceptionHandler
	protected ResponseEntity<?> handleAllException(Exception e) {
		log.error("Exception : {}", e);

		ErrorCode errorCode = ErrorCode._INTERNAL_SERVER_ERROR;

		return newResponse(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getCode());
	}
}
