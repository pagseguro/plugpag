package br.com.uol.pagseguro.plugpag.pagcafe.exception;

public class InvalidContextTypeException extends RuntimeException {
    public InvalidContextTypeException() {
    }

    public InvalidContextTypeException(String message) {
        super(message);
    }

    public InvalidContextTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidContextTypeException(Throwable cause) {
        super(cause);
    }

    public InvalidContextTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
