package io.github.hhnssl.shoppingmall.common.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String code;
    private String message;
    private List<FieldErrorDetail> errors; // 유효성 검사 실패 시 상세 내용

    private ErrorResponse(final ErrorCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.errors = new ArrayList<>();
    }

    // BusinessException의 상세 메시지용
    private ErrorResponse(final ErrorCode code, final String message) {
        this.code = code.getCode();
        this.message = message;
        this.errors = new ArrayList<>();
    }

    //  BindingResult가 있는 경우 (@Valid 실패용)
    private ErrorResponse(final ErrorCode code, final BindingResult bindingResult) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.errors = FieldErrorDetail.of(bindingResult);
    }

    // 팩토리 메서드
    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ErrorCode code, final String message) {
        return new ErrorResponse(code, message);
    }

    public static ErrorResponse of(final ErrorCode code, final BindingResult bindingResult) {
        return new ErrorResponse(code, bindingResult);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldErrorDetail {
        private String field;
        private String value;
        private String reason;

        private FieldErrorDetail(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldErrorDetail> of(final BindingResult bindingResult) {
            final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                .map(error -> new FieldErrorDetail(
                    error.getField(),
                    error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                    error.getDefaultMessage()))
                .collect(Collectors.toList());
        }
    }
}