package com.thyago.taskflow_api.exception;

import org.springframework.http.HttpStatus;

public class ObjectNotFoundException extends NegocioException {

    public static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";
    public static final String MSG_TAREFA_NAO_ENCONTRADA = "Tarefa não encontrada";

    public ObjectNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}