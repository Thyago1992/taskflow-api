package com.thyago.taskflow_api.dto;

import com.thyago.taskflow_api.enums.PrioridadeTarefa;
import com.thyago.taskflow_api.enums.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TarefaRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;

    @NotNull(message = "O status é obrigatório")
    private StatusTarefa status;

    @NotNull(message = "A prioridade é obrigatória")
    private PrioridadeTarefa prioridade;

    @NotNull(message = "A data de validade é obrigatória")
    private LocalDate dataValidade;

    @NotNull(message = "O ID do usuário é obrigatório")
    private Long idUsuario;

}
