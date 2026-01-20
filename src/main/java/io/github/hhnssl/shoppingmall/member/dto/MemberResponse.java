package io.github.hhnssl.shoppingmall.member.dto;

import io.github.hhnssl.shoppingmall.member.domain.Member;
import io.github.hhnssl.shoppingmall.member.domain.MemberStatus;

public record MemberResponse(
    Long id,
    String username,
    String email,
    MemberStatus status
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getMemberId(),
            member.getUsername(),
            member.getEmail(),
            member.getStatus()
        );
    }
}