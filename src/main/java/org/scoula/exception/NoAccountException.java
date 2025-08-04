package org.scoula.exception;

public class NoAccountException extends RuntimeException {

    public NoAccountException() {
        super("해당 사용자의 청약 계좌가 존재하지 않습니다.");
    }

    public NoAccountException(String message) {
        super(message);
    }
}
