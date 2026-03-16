package com.thyago.taskflow_api.dto;

import java.time.LocalDateTime;

public class ErroResponseDTO {

    private int status;
    private String mensagem;
    private LocalDateTime timestamp;

    public ErroResponseDTO(int status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
