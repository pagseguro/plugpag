package br.com.uol.pagseguro.plugpag.pagcafe.exception;

public class MissingDeviceInformationException extends RuntimeException {
    public MissingDeviceInformationException() {
    }

    public MissingDeviceInformationException(String message) {
        super(message);
    }

    public MissingDeviceInformationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingDeviceInformationException(Throwable cause) {
        super(cause);
    }

    public MissingDeviceInformationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
