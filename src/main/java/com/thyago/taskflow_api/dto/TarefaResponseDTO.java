package com.thyago.taskflow_api.dto;

import com.thyago.taskflow_api.enums.PrioridadeTarefa;
import com.thyago.taskflow_api.enums.StatusTarefa;

import java.time.LocalDate;

public record TarefaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        StatusTarefa status,
        PrioridadeTarefa prioridade,
        LocalDate dataCriacao,
        LocalDate dataValidade,
        Long idUsuario) {
}