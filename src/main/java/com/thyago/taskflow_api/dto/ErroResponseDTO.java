package com.thyago.taskflow_api.dto;

import java.time.LocalDateTime;

public record ErroResponseDTO(int status, String mensagem, LocalDateTime timestamp) {

    public ErroResponseDTO(int status, String mensagem) {
        this(status, mensagem, LocalDateTime.now());
    }
}