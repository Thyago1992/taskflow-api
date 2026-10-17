package com.thyago.taskflow_api.exception;

import com.thyago.taskflow_api.enums.StatusTarefa;
import org.springframework.http.HttpStatus;

public class TransicaoStatusInvalidaException extends NegocioException {

    public TransicaoStatusInvalidaException(StatusTarefa origem, StatusTarefa destino) {
        super(HttpStatus.UNPROCESSABLE_ENTITY,
                "Transição de status inválida: " + origem + " para " + destino);
    }
}