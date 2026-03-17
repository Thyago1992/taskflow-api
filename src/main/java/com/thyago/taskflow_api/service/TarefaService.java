package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.dto.TarefaRequestDTO;
import com.thyago.taskflow_api.dto.TarefaResponseDTO;
import com.thyago.taskflow_api.entity.Tarefa;
import com.thyago.taskflow_api.exception.ObjectNotFoundException;
import com.thyago.taskflow_api.repository.TarefaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<TarefaResponseDTO> findAll() {
        return tarefaRepository.findAll()
                .stream()
                .map(tarefa -> modelMapper.map(tarefa, TarefaResponseDTO.class))
                .toList();
    }

    public TarefaResponseDTO findById(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Tarefa não encontrada"));
        return modelMapper.map(tarefa, TarefaResponseDTO.class);
    }

    public TarefaResponseDTO save(TarefaRequestDTO dto) {

        Tarefa tarefa = modelMapper.map(dto, Tarefa.class);
        tarefa.setDataCriacao(LocalDate.now());
        Tarefa salvo = tarefaRepository.save(tarefa);
        return modelMapper.map(salvo, TarefaResponseDTO.class);
    }

    public TarefaResponseDTO update(Long id, TarefaRequestDTO dto) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Tarefa não encontrada"));

        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setStatus(dto.getStatus());
        tarefa.setPrioridade(dto.getPrioridade());
        tarefa.setDataValidade(dto.getDataValidade());

        Tarefa salvo = tarefaRepository.save(tarefa);
        return modelMapper.map(salvo, TarefaResponseDTO.class);
    }

    public void delete(Long id) {
        tarefaRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Tarefa não encontrada"));
        tarefaRepository.deleteById(id);
    }

    public List<TarefaResponseDTO> findByUsuario(Long idUsuario) {
        return tarefaRepository.findByUsuarioId(idUsuario) // ✅
                .stream()
                .map(tarefa -> modelMapper.map(tarefa, TarefaResponseDTO.class))
                .toList();
    }

    public TarefaResponseDTO atualizarStatus(Long id, String status) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Tarefa não encontrada"));
        tarefa.setStatus(status);
        Tarefa salvo = tarefaRepository.save(tarefa);
        return modelMapper.map(salvo, TarefaResponseDTO.class);
    }
}
