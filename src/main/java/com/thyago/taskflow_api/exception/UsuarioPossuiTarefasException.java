package com.thyago.taskflow_api.exception;

import org.springframework.http.HttpStatus;

public class UsuarioPossuiTarefasException extends NegocioException {

    public static final String MSG_USUARIO_POSSUI_TAREFAS =
            "Não é possível excluir um usuário que possui tarefas";

    public UsuarioPossuiTarefasException() {
        super(HttpStatus.CONFLICT, MSG_USUARIO_POSSUI_TAREFAS);
    }
}