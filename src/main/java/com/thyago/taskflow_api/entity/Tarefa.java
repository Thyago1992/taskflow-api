package com.thyago.taskflow_api.entity;

import com.thyago.taskflow_api.enums.PrioridadeTarefa;
import com.thyago.taskflow_api.enums.StatusTarefa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
    private StatusTarefa status;
    private PrioridadeTarefa prioridade;
    private LocalDate dataCriacao;
    private LocalDate dataValidade;
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Override
    public String toString() {
        return """
            Tarefa:
              ID:           %d
              Titulo:       %s
              Descricao:    %s
              Status:       %s
              Prioridade:   %s
              Data Criacao: %s
              Data Validade:%s
              ID Usuario:   %d
            """.formatted(id, titulo, descricao, status, prioridade, dataCriacao, dataValidade, usuario.getId());
    }
}
