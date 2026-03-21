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

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTarefa status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeTarefa prioridade;

    @Column(nullable = false)
    private LocalDate dataCriacao;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDate.now();
    }

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
              ID Usuario:   %s
            """.formatted(id, titulo, descricao, status, prioridade, dataCriacao, dataValidade, usuario != null ?
                usuario.getId() : null
        );
    }
}
