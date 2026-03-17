package com.thyago.taskflow_api.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
    private String status;
    private String prioridade;
    private LocalDate dataCriacao;
    private LocalDate dataValidade;
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    public Tarefa(Long id, String titulo, String descricao, String status, String prioridade, LocalDate dataCriacao, LocalDate dataValidade, Usuario usuario) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.prioridade = prioridade;
        this.dataCriacao = dataCriacao;
        this.dataValidade = dataValidade;
        this.usuario = usuario;
    }

    public Tarefa() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
              ID Usuario:   %d
            """.formatted(id, titulo, descricao, status, prioridade, dataCriacao, dataValidade, usuario.getId());
    }
}
