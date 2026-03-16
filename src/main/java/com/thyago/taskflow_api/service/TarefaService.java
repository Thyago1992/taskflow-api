package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.dto.TarefaRequestDTO;
import com.thyago.taskflow_api.dto.TarefaResponseDTO;
import com.thyago.taskflow_api.entity.Tarefa;
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
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        return modelMapper.map(tarefa, TarefaResponseDTO.class);
    }

    public TarefaResponseDTO save(TarefaRequestDTO dto) {

        Tarefa tarefa = modelMapper.map(dto, Tarefa.class);
        tarefa.setDataCriacao(LocalDate.now());
        Tarefa salvo = tarefaRepository.save(tarefa);
        return modelMapper.map(salvo, TarefaResponseDTO.class);
    }

    public TarefaResponseDTO update(Long id, TarefaRequestDTO dto) {
        tarefaRepository.findById(id).orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        Tarefa tarefa = modelMapper.map(dto, Tarefa.class);
        tarefa.setId(id);
        Tarefa salvo = tarefaRepository.save(tarefa);
        return modelMapper.map(salvo, TarefaResponseDTO.class);
    }

    public void delete(Long id) {
        tarefaRepository.findById(id).orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        tarefaRepository.deleteById(id);
    }

    public List<TarefaResponseDTO> findByUsuario(Long idUsuario) {
        return tarefaRepository.findByIdUsuario(idUsuario) // ✅
                .stream()
                .map(tarefa -> modelMapper.map(tarefa, TarefaResponseDTO.class))
                .toList();
    }

    public TarefaResponseDTO atualizarStatus(Long id, String status) {
        tarefaRepository.findById(id).orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
        Tarefa tarefa = modelMapper.map(tarefaRepository.findById(id).get(), Tarefa.class);
        tarefa.setStatus(status);
        Tarefa salvo = tarefaRepository.save(tarefa);
        return modelMapper.map(salvo, TarefaResponseDTO.class);
    }
}
