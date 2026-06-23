package com.rocea.batch.pagosclientes.exception;

public class AppException extends RuntimeException {

    private final String codigo;
    private final boolean retryable;

    protected AppException(String codigo, String message, boolean retryable) {
        super(message);
        this.codigo = codigo;
        this.retryable = retryable;
    }

    protected AppException(String codigo, String message, boolean retryable, Throwable cause) {
        super(message, cause);
        this.codigo = codigo;
        this.retryable = retryable;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isRetryable() {
        return retryable;
    }
}