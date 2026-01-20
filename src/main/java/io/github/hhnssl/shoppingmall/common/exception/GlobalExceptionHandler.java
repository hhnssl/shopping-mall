package io.github.hhnssl.shoppingmall.common.exception;

import io.github.hhnssl.shoppingmall.member.domain.exception.InvalidMemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생
     * 예: @Email 형식 위반, @NotBlank 위반 등
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("handleMethodArgumentNotValidException", e);

        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        final ErrorResponse response = ErrorResponse.of(errorCode, e.getBindingResult());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("handleHttpMessageNotReadableException", e);

        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        final ErrorResponse response = ErrorResponse.of(errorCode);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("handleMethodArgumentTypeMismatchException", e);

        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        final ErrorResponse response = ErrorResponse.of(errorCode);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    /**
     * 도메인 불변식 위반(순수 도메인 예외) -> 공통 에러 코드로 매핑
     * 예: Member.register()에서 null/blank 검증 실패 등
     */
    @ExceptionHandler(InvalidMemberException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidMemberException(InvalidMemberException e) {
        log.warn("handleInvalidMemberException message={}", e.getMessage());

        final ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        final ErrorResponse response = ErrorResponse.of(errorCode, e.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
        log.warn("handleBusinessException code={}, status={}, message={}",
            errorCode.getCode(), errorCode.getStatus(), e.getMessage());

        final ErrorResponse response = ErrorResponse.of(errorCode, e.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("handleHttpRequestMethodNotSupportedException", e);

        final ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        final ErrorResponse response = ErrorResponse.of(errorCode);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);

        final ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        final ErrorResponse response = ErrorResponse.of(errorCode);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}
