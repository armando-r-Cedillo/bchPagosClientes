package com.rocea.batch.pagosclientes.exception;

public class PagoValidationException extends AppException {

    protected PagoValidationException(String codigo, String message, boolean retryable, Throwable cause) {
        super(codigo, message, retryable, cause);
    }
}