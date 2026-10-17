package com.thyago.taskflow_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import com.thyago.taskflow_api.config.FusoPadrao;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false,  unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDateTime.now(FusoPadrao.ZONA);
    }

    @Override
    public String toString() {
        return """
            Usuario:
              ID:    %d
              Nome:  %s
              Email: %s
              Data:  %s
            """.formatted(id, nome, email, dataCriacao);
    }
}
