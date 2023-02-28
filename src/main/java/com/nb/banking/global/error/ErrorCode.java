package com.nb.banking.global.error;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	/* 공통 오류 */
	_INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "C000", "서버 에러, 관리자에게 문의 바랍니다"),
	_BAD_REQUEST(BAD_REQUEST, "C001", "잘못된 요청입니다"),
	_UNAUTHORIZED(UNAUTHORIZED, "C002", "권한이 없습니다"),

	_METHOD_NOT_ALLOWED(METHOD_NOT_ALLOWED, "C003", "지원하지 않는 Http Method 입니다"),
	_UNSUPPORTED_MEDIA_TYPE(UNSUPPORTED_MEDIA_TYPE, "C004", "지원하지 않는 Http Media PendingType 입니다"),
	_INVALID_REQUEST_PARAMETER(BAD_REQUEST, "C005", "유효하지 않은 Request Parameter 입니다"),

	NOT_MULTIPART_HEADER(BAD_REQUEST, "C008", "Multipart 헤더가 아닙니다"),
	AMAZON_ACCESS_DENIED(FORBIDDEN, "C009", "Amazon S3 접근이 거부되었습니다"),

	/* Validation 오류 */
	PARAMETER_NOT_VALID(BAD_REQUEST, "P000", "인자가 유효하지 않습니다"),
	PASSWORD_CONFIRM_NOT_VALID(BAD_REQUEST, "P001", "입력된 비밀번호와 일치하지 않습니다."),

	/* Database 관련 오류 */
	DUPLICATE_RESOURCE(CONFLICT, "D001", "데이터가 이미 존재합니다"),

	/* Auth 관련 오류 */
	NO_TOKEN(UNAUTHORIZED, "AUTH000", "토큰이 존재하지 않습니다"),
	EXPIRED_TOKEN(UNAUTHORIZED, "AUTH001", "만료된 엑세스 토큰입니다"),
	INVALID_REFRESH_TOKEN(UNAUTHORIZED, "AUTH002", "리프레시 토큰이 유효하지 않습니다"),
	MISMATCH_REFRESH_TOKEN(UNAUTHORIZED, "AUTH003", "리프레시 토큰의 유저 정보가 일치하지 않습니다"),
	EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "AUTH004", "만료된 리프레시 토큰입니다"),
	INVALID_AUTH_TOKEN(UNAUTHORIZED, "AUTH005", "권한 정보가 없는 토큰입니다"),
	UNAUTHORIZED_USER(UNAUTHORIZED, "AUTH006", "로그인이 필요합니다"),
	REFRESH_TOKEN_NOT_FOUND(UNAUTHORIZED, "AUTH007", "로그아웃 된 사용자입니다"),
	FORBIDDEN_USER(FORBIDDEN, "AUTH008", "권한이 없는 유저입니다"),
	UNSUPPORTED_TOKEN(UNAUTHORIZED, "AUTH009", "지원하지 않는 토큰입니다"),
	INVALID_SIGNATURE(UNAUTHORIZED, "AUTH010", "잘못된 JWT 서명입니다"),
	MISMATCH_VERIFICATION_CODE(UNAUTHORIZED, "AUTH011", "인증번호가 일치하지 않습니다"),
	EXPIRED_VERIFICATION_CODE(UNAUTHORIZED, "AUTH012", "인증번호가 만료되었습니다"),
	INVALID_USER_TOKEN(UNAUTHORIZED, "AUTH013", "서버에 토큰과 일치하는 정보가 없습니다"),

	LOGIN_FAILED(UNAUTHORIZED, "AUTH013", "로그인에 실패했습니다"),
	SOCIAL_ALREADY_EXIST(BAD_REQUEST, "AUTH014", "소셜로 가입된 회원입니다"),
	INVALID_ACCESS_TOKEN(UNAUTHORIZED, "AUTH015", "유효하지 않은 엑세스 토큰입니다"),
	COMMON_ALREADY_EXIST(BAD_REQUEST, "AUTH016", "일반으로 가입된 회원입니다"),

	/* 이미지 관련 오류 */
	INVALID_FILE_EXTENSION(BAD_REQUEST, "FILE000", "잘못된 파일 확장자명입니다"),
	FILE_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "FILE001", "파일 업로드에 실패했습니다"),

	/* Member 관련 오류 */
	MEMBER_ALREADY_EXIST(BAD_REQUEST, "M002", "이미 가입된 유저입니다"),
	MEMBER_NOT_FOUND(NOT_FOUND, "M003", "해당 유저 정보를 찾을 수 없습니다"),
	COUNTRY_NOT_FOUND(NOT_FOUND, "M004", "해당 국가를 찾을 수 없습니다"),
	CONNECTION_ALREADY_EXIST(BAD_REQUEST, "M004", "이미 등록된 친구입니다"),
	CONNECTION_NOT_EXIST(BAD_REQUEST, "M004", "친구로 등록되어 있지 않습니다"),

	/* Account 관련 오류 */
	ACCOUNT_NOT_FOUND(NOT_FOUND, "A001", "해당 하는 계좌정보를 찾을 수 없습니다"),
	ACCOUNT_INSUFFICIENT(BAD_REQUEST, "A002", "계좌의 잔액이 부족합니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String detail;
}
