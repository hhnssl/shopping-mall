package io.github.hhnssl.shoppingmall.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {

    ACTIVE("정상 회원", true),
    DELETED("탈퇴 회원", false);

    private final String description;
    private final boolean usable;
}