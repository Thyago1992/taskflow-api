package com.thyago.taskflow_api.exception;

import org.springframework.http.HttpStatus;

public class DataValidadeInvalidaException extends NegocioException {

    public static final String MSG_DATA_VALIDADE_PASSADA = "A data de validade não pode estar no passado";

    public DataValidadeInvalidaException() {
        super(HttpStatus.BAD_REQUEST, MSG_DATA_VALIDADE_PASSADA);
    }
}