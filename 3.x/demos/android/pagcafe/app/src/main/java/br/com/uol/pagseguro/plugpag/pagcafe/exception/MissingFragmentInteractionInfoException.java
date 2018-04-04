package br.com.uol.pagseguro.plugpag.pagcafe.exception;

public class MissingFragmentInteractionInfoException extends RuntimeException {

    public MissingFragmentInteractionInfoException() {
    }

    public MissingFragmentInteractionInfoException(String message) {
        super(message);
    }

    public MissingFragmentInteractionInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingFragmentInteractionInfoException(Throwable cause) {
        super(cause);
    }

    public MissingFragmentInteractionInfoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
