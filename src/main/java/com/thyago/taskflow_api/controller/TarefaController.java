package com.thyago.taskflow_api.controller;

import com.thyago.taskflow_api.dto.PaginaResponseDTO;
import com.thyago.taskflow_api.dto.TarefaRequestDTO;
import com.thyago.taskflow_api.dto.TarefaResponseDTO;
import com.thyago.taskflow_api.enums.StatusTarefa;
import com.thyago.taskflow_api.service.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @Operation(description = "Recupera as tarefas de forma paginada, com filtro opcional por status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de tarefas retornada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Status ou campo de ordenação inválido.")
    })
    @GetMapping
    public ResponseEntity<PaginaResponseDTO<TarefaResponseDTO>> findAll(
            @RequestParam(required = false) StatusTarefa status,
            @ParameterObject @PageableDefault(size = 10, sort = "dataValidade") Pageable pageable) {
        return ResponseEntity.ok(tarefaService.findAll(status, pageable));
    }

    @Operation(description = "Recupera os detalhes de uma tarefa específica com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada e retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada para o ID fornecido.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> findById(@PathVariable Long id) {
        TarefaResponseDTO tarefa = tarefaService.findById(id);
        return ResponseEntity.ok(tarefa);
    }

    @Operation(description = "Recupera as tarefas com data de validade vencida que ainda não foram concluídas " +
            "nem canceladas, da mais atrasada para a menos atrasada.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de tarefas atrasadas retornada com " +
            "sucesso.")})
    @GetMapping("/atrasadas")
    public ResponseEntity<List<TarefaResponseDTO>> findAtrasadas() {
        return ResponseEntity.ok(tarefaService.findAtrasadas());
    }

    @Operation(description = "Cria uma nova tarefa no sistema com os dados fornecidos no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida. Verifique os dados fornecidos.")
    })
    @PostMapping
    public ResponseEntity<TarefaResponseDTO> save(@Valid @RequestBody TarefaRequestDTO dto) {
        TarefaResponseDTO criado = tarefaService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(description = "Recupera a lista de tarefas associadas a um usuário específico com base no ID do usuário " +
            "fornecido.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Lista de tarefas do usuário retornada com sucesso."),
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado para o ID fornecido.")})
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<TarefaResponseDTO>> findByUsuario(@PathVariable Long idUsuario) {
        List<TarefaResponseDTO> tarefas = tarefaService.findByUsuario(idUsuario);
        return ResponseEntity.ok(tarefas);
    }

    // ✅ correto — todos os @ApiResponse dentro do array
    @Operation(description = "Atualiza os dados de uma tarefa existente com base no ID fornecido e nos novos dados no " +
            "corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida. Verifique os dados fornecidos."),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada para o ID fornecido.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TarefaRequestDTO dto) {
        TarefaResponseDTO atualizado = tarefaService.update(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(description = "Remove uma tarefa do sistema com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa deletada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada para o ID fornecido.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tarefaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Atualiza o status de uma tarefa existente com base no ID fornecido e no novo status " +
            "passado como parâmetro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status da tarefa atualizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida. Verifique os dados fornecidos."),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada para o ID fornecido.")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<TarefaResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusTarefa status) {
        return ResponseEntity.ok(tarefaService.atualizarStatus(id, status));
    }
}
