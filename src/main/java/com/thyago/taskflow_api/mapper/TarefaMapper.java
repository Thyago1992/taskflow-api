package com.thyago.taskflow_api.mapper;

import com.thyago.taskflow_api.dto.TarefaRequestDTO;
import com.thyago.taskflow_api.dto.TarefaResponseDTO;
import com.thyago.taskflow_api.entity.Tarefa;
import com.thyago.taskflow_api.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class TarefaMapper {

    public void preencher(Tarefa tarefa, TarefaRequestDTO dto, Usuario usuario) {
        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setPrioridade(dto.prioridade());
        tarefa.setDataValidade(dto.dataValidade());
        tarefa.setUsuario(usuario);
    }

    public TarefaResponseDTO toResponse(Tarefa tarefa) {
        return new TarefaResponseDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getPrioridade(),
                tarefa.getDataCriacao(),
                tarefa.getDataValidade(),
                tarefa.getUsuario().getId());
    }
}