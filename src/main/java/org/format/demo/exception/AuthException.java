package org.format.demo.exception;

public class AuthException extends RuntimeException {

    public AuthException() {
        super();
    }

    public AuthException(String msg) {
        super(msg);
    }

    public AuthException(Throwable e) {
        super(e);
    }

    public AuthException(String msg, Throwable e) {
        super(msg, e);
    }

}
