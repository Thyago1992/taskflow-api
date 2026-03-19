package com.thyago.taskflow_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private String nome;
    private String email;
    private LocalDateTime dataCriacao;

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
