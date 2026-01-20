package io.github.hhnssl.shoppingmall.member.domain.exception;

public class InvalidMemberException extends RuntimeException {

    public InvalidMemberException(String message) {
        super(message);
    }
}