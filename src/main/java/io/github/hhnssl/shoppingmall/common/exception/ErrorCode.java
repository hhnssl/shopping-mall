package io.github.hhnssl.shoppingmall.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),

    // 회원 관련
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "존재하지 않는 회원입니다."),
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "M002", "이미 존재하는 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "M003", "아이디 또는 비밀번호가 틀렸습니다."),
    INVALID_MEMBER_EMAIL(HttpStatus.BAD_REQUEST, "M004", "이메일은 필수입니다."),
    INVALID_MEMBER_USERNAME(HttpStatus.BAD_REQUEST, "M005", "이름은 필수입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}