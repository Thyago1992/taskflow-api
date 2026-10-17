package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.dto.PaginaResponseDTO;
import com.thyago.taskflow_api.dto.TarefaRequestDTO;
import com.thyago.taskflow_api.dto.TarefaResponseDTO;
import com.thyago.taskflow_api.entity.Tarefa;
import com.thyago.taskflow_api.entity.Usuario;
import com.thyago.taskflow_api.enums.StatusTarefa;
import com.thyago.taskflow_api.exception.DataValidadeInvalidaException;
import com.thyago.taskflow_api.exception.ObjectNotFoundException;
import com.thyago.taskflow_api.mapper.TarefaMapper;
import com.thyago.taskflow_api.repository.TarefaRepository;
import com.thyago.taskflow_api.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    private final UsuarioRepository usuarioRepository;

    private final TarefaMapper tarefaMapper;

    private static final List<StatusTarefa> STATUS_FINAIS =
            List.of(StatusTarefa.CONCLUIDA, StatusTarefa.CANCELADA);

    private final Clock clock;

    public TarefaService(TarefaRepository tarefaRepository,
                         UsuarioRepository usuarioRepository,
                         TarefaMapper tarefaMapper,
                         Clock clock) {
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
        this.tarefaMapper = tarefaMapper;
        this.clock = clock;
    }

    public PaginaResponseDTO<TarefaResponseDTO> findAll(StatusTarefa status, Pageable pageable) {
        Page<Tarefa> pagina;
        if (status == null) {
            pagina = tarefaRepository.findAll(pageable);
        } else {
            pagina = tarefaRepository.findByStatus(status, pageable);
        }
        return PaginaResponseDTO.de(pagina.map(tarefaMapper::toResponse));
    }

    public TarefaResponseDTO findById(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.MSG_TAREFA_NAO_ENCONTRADA));
        return tarefaMapper.toResponse(tarefa);
    }

    public TarefaResponseDTO save(TarefaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.MSG_USUARIO_NAO_ENCONTRADO));

        validarDataValidade(dto.dataValidade());

        // O status inicial é sempre PENDENTE (definido na entidade)
        Tarefa tarefa = new Tarefa();
        tarefaMapper.preencher(tarefa, dto, usuario);

        Tarefa salvo = tarefaRepository.save(tarefa);
        return tarefaMapper.toResponse(salvo);
    }

    public TarefaResponseDTO update(Long id, TarefaRequestDTO dto) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.MSG_TAREFA_NAO_ENCONTRADA));

        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.MSG_USUARIO_NAO_ENCONTRADO));

        // Só valida a data se ela mudou: assim dá para editar uma tarefa que já está atrasada
        if (!dto.dataValidade().equals(tarefa.getDataValidade())) {
            validarDataValidade(dto.dataValidade());
        }

        // Primeiro valida a transição, para falhar antes de alterar os outros campos
        tarefa.alterarStatus(dto.status());
        tarefaMapper.preencher(tarefa, dto, usuario);

        Tarefa salvo = tarefaRepository.save(tarefa);
        return tarefaMapper.toResponse(salvo);
    }

    public void delete(Long id) {
        tarefaRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.
                MSG_TAREFA_NAO_ENCONTRADA));
        tarefaRepository.deleteById(id);
    }

    public List<TarefaResponseDTO> findByUsuario(Long idUsuario) {
        usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.MSG_USUARIO_NAO_ENCONTRADO));

        return tarefaRepository.findByUsuarioId(idUsuario)
                .stream()
                .map(tarefaMapper::toResponse)
                .toList();
    }

    public List<TarefaResponseDTO> findAtrasadas() {
        LocalDate hoje = LocalDate.now(clock);
        return tarefaRepository
                .findByDataValidadeBeforeAndStatusNotInOrderByDataValidadeAsc(hoje, STATUS_FINAIS)
                .stream()
                .map(tarefaMapper::toResponse)
                .toList();
    }

    public TarefaResponseDTO atualizarStatus(Long id, StatusTarefa status) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.MSG_TAREFA_NAO_ENCONTRADA));
        tarefa.alterarStatus(status);
        Tarefa salvo = tarefaRepository.save(tarefa);
        return tarefaMapper.toResponse(salvo);
    }

    private void validarDataValidade(LocalDate dataValidade) {
        if (dataValidade.isBefore(LocalDate.now(clock))) {
            throw new DataValidadeInvalidaException();
        }
    }
}