package com.thyago.taskflow_api.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public static final String MSG_EMAIL_EXISTENTE = "Email já cadastrado";

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
