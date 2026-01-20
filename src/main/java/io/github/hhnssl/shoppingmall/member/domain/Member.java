package io.github.hhnssl.shoppingmall.member.domain;

import io.github.hhnssl.shoppingmall.common.exception.BusinessException;
import io.github.hhnssl.shoppingmall.common.exception.ErrorCode;
import io.github.hhnssl.shoppingmall.member.domain.exception.InvalidMemberException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Member {

    private Long memberId;
    private String email;
    private String username;
    private String password;
    private MemberStatus status;

    public static Member register(String email, String username, String password) {
        if (email == null || email.isBlank()) {
            throw new InvalidMemberException("이메일은 필수 입력 값입니다.");
        }
        if (username == null || username.isBlank()) {
            throw new InvalidMemberException("사용자 이름은 필수입니다.");
        }
        return new Member(null, email, username, password, MemberStatus.ACTIVE);
    }

    public void changeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        this.username = username;
    }

    public void changePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        this.password = password;
    }

    public void delete() {
        this.status = MemberStatus.DELETED;
    }

    public void assignId(Long memberId) {
        if (this.memberId != null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        this.memberId = memberId;
    }

    public boolean isActive() {
        return this.status.isUsable();
    }
}
