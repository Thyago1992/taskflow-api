package com.thyago.taskflow_api.controller;

import com.thyago.taskflow_api.dto.TarefaRequestDTO;
import com.thyago.taskflow_api.dto.TarefaResponseDTO;
import com.thyago.taskflow_api.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @GetMapping
    public ResponseEntity<List<TarefaResponseDTO>> findAll() {
        List<TarefaResponseDTO> tarefas = tarefaService.findAll();
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> findById(@PathVariable Long id) {
        TarefaResponseDTO tarefa = tarefaService.findById(id);
        return ResponseEntity.ok(tarefa);
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> save(@RequestBody TarefaRequestDTO dto) {
        TarefaResponseDTO criado = tarefaService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<TarefaResponseDTO>> findByUsuario(@PathVariable Long idUsuario) {
        List<TarefaResponseDTO> tarefas = tarefaService.findByUsuario(idUsuario);
        return ResponseEntity.ok(tarefas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody TarefaRequestDTO dto) {
        TarefaResponseDTO atualizado = tarefaService.update(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tarefaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
