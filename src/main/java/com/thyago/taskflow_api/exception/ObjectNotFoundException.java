package com.thyago.taskflow_api.exception;

public class ObjectNotFoundException extends RuntimeException{
    public static final String MSG_USUARIO_NAO_ENCONTRADO = "Usuário não encontrado";
    public static final String MSG_TAREFA_NAO_ENCONTRADA = "Tarefa não encontrada";

    public ObjectNotFoundException(String message) {
        super(message);
    }
}
