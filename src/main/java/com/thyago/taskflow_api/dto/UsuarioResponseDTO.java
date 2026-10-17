package com.thyago.taskflow_api.dto;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        LocalDateTime dataCriacao) {
}