package io.github.hhnssl.shoppingmall.member.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberUpdateReq(

    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
    String username,

    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하이어야 합니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    String password
) {
    // Compact Constructor: 레코드의 값을 정규화(trim)하기 가장 좋은 위치
    public MemberUpdateReq {
        username = username == null ? null : username.trim();
        password = password == null ? null : password.trim();
    }
}