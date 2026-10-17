package com.thyago.taskflow_api.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends NegocioException {

    public static final String MSG_EMAIL_EXISTENTE = "Email já cadastrado";

    public EmailAlreadyExistsException() {
        super(HttpStatus.CONFLICT, MSG_EMAIL_EXISTENTE);
    }
}