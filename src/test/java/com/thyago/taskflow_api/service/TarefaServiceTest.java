package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.config.FusoPadrao;
import com.thyago.taskflow_api.dto.TarefaRequestDTO;
import com.thyago.taskflow_api.dto.TarefaResponseDTO;
import com.thyago.taskflow_api.entity.Tarefa;
import com.thyago.taskflow_api.entity.Usuario;
import com.thyago.taskflow_api.enums.PrioridadeTarefa;
import com.thyago.taskflow_api.enums.StatusTarefa;
import com.thyago.taskflow_api.exception.DataValidadeInvalidaException;
import com.thyago.taskflow_api.exception.ObjectNotFoundException;
import com.thyago.taskflow_api.exception.TransicaoStatusInvalidaException;
import com.thyago.taskflow_api.mapper.TarefaMapper;
import com.thyago.taskflow_api.repository.TarefaRepository;
import com.thyago.taskflow_api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    private TarefaService tarefaService;

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TarefaMapper tarefaMapper;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.system(FusoPadrao.ZONA);
        tarefaService = new TarefaService(tarefaRepository, usuarioRepository, tarefaMapper, clock);
    }

    // ======================== findAll ========================

    @Test
    void deveRetornarTodasTarefas() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(tarefaRepository.findAll()).thenReturn(List.of(tarefa));
        Mockito.when(tarefaMapper.toResponse(tarefa)).thenReturn(dto);

        // --- ACT ---
        List<TarefaResponseDTO> resultado = tarefaService.findAll();

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Tarefa 1", resultado.get(0).titulo());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findAll();
    }

    // ======================== findById ========================

    @Test
    void deveRetornarTarefaPorId() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        Mockito.when(tarefaMapper.toResponse(tarefa)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.findById(1L);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Tarefa 1", resultado.titulo());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoTarefaNaoEncontradaPorId() {
        // --- ARRANGE ---
        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.findById(99L));

        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(99L);
    }

    // ======================== save ========================

    @Test
    void deveSalvarTarefa() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().plusDays(7), 1L);
        Tarefa tarefaSalva = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.save(Mockito.any(Tarefa.class))).thenReturn(tarefaSalva);
        Mockito.when(tarefaMapper.toResponse(tarefaSalva)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.save(request);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals("Tarefa 1", resultado.titulo());
        assertEquals(1L, resultado.idUsuario());

        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaMapper, Mockito.times(1))
                .preencher(Mockito.any(Tarefa.class), Mockito.eq(request), Mockito.eq(usuario));
        Mockito.verify(tarefaRepository, Mockito.times(1)).save(Mockito.any(Tarefa.class));
    }

    @Test
    void deveLancarExcecaoAoSalvarTarefaComUsuarioInexistente() {
        // --- ARRANGE ---
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().plusDays(7), 99L);

        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.save(request));

        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveLancarExcecaoAoSalvarComDataValidadeNoPassado() {
        // --- ARRANGE ---
        TarefaRequestDTO request = new TarefaRequestDTO(
                "Estudar", "Revisar JPA", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA,
                LocalDate.now().minusDays(1), 1L);
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // --- ACT + ASSERT ---
        assertThrows(DataValidadeInvalidaException.class, () -> tarefaService.save(request));

        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }

    // ======================== update ========================

    @Test
    void deveAtualizarTarefa() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa Atualizada", "Nova descrição", StatusTarefa.EM_ANDAMENTO, PrioridadeTarefa.MEDIA, LocalDate.now().plusDays(10), 1L);

        Tarefa tarefaExistente = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        Tarefa tarefaSalva = new Tarefa(1L, "Tarefa Atualizada", "Nova descrição", StatusTarefa.EM_ANDAMENTO, PrioridadeTarefa.MEDIA, LocalDate.now(), LocalDate.now().plusDays(10), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa Atualizada", "Nova descrição", StatusTarefa.EM_ANDAMENTO, PrioridadeTarefa.MEDIA, LocalDate.now(), LocalDate.now().plusDays(10), 1L);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefaExistente));
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.save(tarefaExistente)).thenReturn(tarefaSalva);
        Mockito.when(tarefaMapper.toResponse(tarefaSalva)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.update(1L, request);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals("Tarefa Atualizada", resultado.titulo());
        assertEquals(StatusTarefa.EM_ANDAMENTO, resultado.status());
        // O status da entidade foi alterado pela regra de transição
        assertEquals(StatusTarefa.EM_ANDAMENTO, tarefaExistente.getStatus());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaMapper, Mockito.times(1)).preencher(tarefaExistente, request, usuario);
        Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefaExistente);
    }

    @Test
    void devePermitirAtualizarTarefaAtrasadaSemMudarDataValidade() {
        // --- ARRANGE ---
        // A data de validade já passou, mas como não muda, a edição deve ser aceita.
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        LocalDate dataVencida = LocalDate.now().minusDays(5);
        TarefaRequestDTO request = new TarefaRequestDTO("Novo título", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, dataVencida, 1L);
        Tarefa tarefaExistente = new Tarefa(1L, "Tarefa", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().minusDays(10), dataVencida, usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Novo título", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().minusDays(10), dataVencida, 1L);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefaExistente));
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.save(tarefaExistente)).thenReturn(tarefaExistente);
        Mockito.when(tarefaMapper.toResponse(tarefaExistente)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.update(1L, request);

        // --- ASSERT ---
        assertEquals("Novo título", resultado.titulo());
        Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefaExistente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarTarefaNaoEncontrada() {
        // --- ARRANGE ---
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().plusDays(7), 1L);

        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.update(99L, request));

        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarTarefaComUsuarioInexistente() {
        // --- ARRANGE ---
        // Tarefa existe, mas o novo idUsuario informado no DTO não existe.
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().plusDays(7), 99L);
        Tarefa tarefaExistente = new Tarefa(1L, "Tarefa", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefaExistente));
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.update(1L, request));

        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }

    // ======================== delete ========================

    @Test
    void deveDeletarTarefa() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        // --- ACT ---
        tarefaService.delete(1L);

        // --- ASSERT ---
        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarTarefaNaoEncontrada() {
        // --- ARRANGE ---
        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.delete(99L));

        Mockito.verify(tarefaRepository, Mockito.never()).deleteById(Mockito.any());
    }

    // ======================== findByUsuario ========================

    @Test
    void deveRetornarTarefasPorUsuario() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.findByUsuarioId(1L)).thenReturn(List.of(tarefa));
        Mockito.when(tarefaMapper.toResponse(tarefa)).thenReturn(dto);

        // --- ACT ---
        List<TarefaResponseDTO> resultado = tarefaService.findByUsuario(1L);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).idUsuario());

        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaRepository, Mockito.times(1)).findByUsuarioId(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarTarefasDeUsuarioInexistente() {
        // --- ARRANGE ---
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.findByUsuario(99L));

        // Usuário não encontrado = não deve nem consultar as tarefas.
        Mockito.verify(tarefaRepository, Mockito.never()).findByUsuarioId(Mockito.any());
    }

    // ======================== atualizarStatus ========================

    @Test
    void deveAtualizarStatusDaTarefa() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        Tarefa tarefaSalva = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.CONCLUIDA, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.CONCLUIDA, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        Mockito.when(tarefaRepository.save(tarefa)).thenReturn(tarefaSalva);
        Mockito.when(tarefaMapper.toResponse(tarefaSalva)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.atualizarStatus(1L, StatusTarefa.CONCLUIDA);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(StatusTarefa.CONCLUIDA, resultado.status());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefa);
    }

    @Test
    void deveLancarExcecaoAoAtualizarStatusDeTarefaNaoEncontrada() {
        // --- ARRANGE ---
        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.atualizarStatus(99L, StatusTarefa.CONCLUIDA));

        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarStatusComTransicaoInvalida() {
        // --- ARRANGE ---
        // Tarefa já concluída não pode voltar para pendente.
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.CONCLUIDA, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        // --- ACT + ASSERT ---
        assertThrows(TransicaoStatusInvalidaException.class,
                () -> tarefaService.atualizarStatus(1L, StatusTarefa.PENDENTE));

        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }
}