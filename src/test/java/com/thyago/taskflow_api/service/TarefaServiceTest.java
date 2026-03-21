package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.dto.TarefaRequestDTO;
import com.thyago.taskflow_api.dto.TarefaResponseDTO;
import com.thyago.taskflow_api.entity.Tarefa;
import com.thyago.taskflow_api.entity.Usuario;
import com.thyago.taskflow_api.enums.PrioridadeTarefa;
import com.thyago.taskflow_api.enums.StatusTarefa;
import com.thyago.taskflow_api.exception.ObjectNotFoundException;
import com.thyago.taskflow_api.repository.TarefaRepository;
import com.thyago.taskflow_api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @InjectMocks
    private TarefaService tarefaService;

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    // ======================== findAll ========================

    @Test
    public void deveRetornarTodasTarefas() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(tarefaRepository.findAll()).thenReturn(List.of(tarefa));
        Mockito.when(modelMapper.map(tarefa, TarefaResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        List<TarefaResponseDTO> resultado = tarefaService.findAll();

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Tarefa 1", resultado.get(0).getTitulo());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findAll();
    }

    // ======================== findById ========================

    @Test
    public void deveRetornarTarefaPorId() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        Mockito.when(modelMapper.map(tarefa, TarefaResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.findById(1L);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Tarefa 1", resultado.getTitulo());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void deveLancarExcecaoQuandoTarefaNaoEncontradaPorId() {
        // --- ARRANGE ---
        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.findById(99L));

        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(99L);
    }

    // ======================== save ========================

    @Test
    public void deveSalvarTarefa() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().plusDays(7), 1L);
        Tarefa tarefaSalva = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.save(Mockito.any(Tarefa.class))).thenReturn(tarefaSalva);
        Mockito.when(modelMapper.map(tarefaSalva, TarefaResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.save(request);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals("Tarefa 1", resultado.getTitulo());
        assertEquals(1L, resultado.getIdUsuario());

        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaRepository, Mockito.times(1)).save(Mockito.any(Tarefa.class));
    }

    @Test
    public void deveLancarExcecaoAoSalvarTarefaComUsuarioInexistente() {
        // --- ARRANGE ---
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().plusDays(7), 99L);

        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.save(request));

        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }

    // ======================== update ========================

    @Test
    public void deveAtualizarTarefa() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa Atualizada", "Nova descrição", StatusTarefa.EM_ANDAMENTO, PrioridadeTarefa.MEDIA, LocalDate.now().plusDays(10), 1L);

        Tarefa tarefaExistente = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        Tarefa tarefaSalva = new Tarefa(1L, "Tarefa Atualizada", "Nova descrição", StatusTarefa.EM_ANDAMENTO, PrioridadeTarefa.MEDIA, LocalDate.now(), LocalDate.now().plusDays(10), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa Atualizada", "Nova descrição", StatusTarefa.EM_ANDAMENTO, PrioridadeTarefa.MEDIA, LocalDate.now(), LocalDate.now().plusDays(10), 1L);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefaExistente));
        // update() agora busca o usuário — precisa estar mockado
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.save(tarefaExistente)).thenReturn(tarefaSalva);
        Mockito.when(modelMapper.map(tarefaSalva, TarefaResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.update(1L, request);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals("Tarefa Atualizada", resultado.getTitulo());
        assertEquals(StatusTarefa.EM_ANDAMENTO, resultado.getStatus());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefaExistente);
    }

    @Test
    public void deveLancarExcecaoAoAtualizarTarefaNaoEncontrada() {
        // --- ARRANGE ---
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().plusDays(7), 1L);

        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.update(99L, request));

        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void deveLancarExcecaoAoAtualizarTarefaComUsuarioInexistente() {
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
    public void deveDeletarTarefa() {
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
    public void deveLancarExcecaoAoDeletarTarefaNaoEncontrada() {
        // --- ARRANGE ---
        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.delete(99L));

        Mockito.verify(tarefaRepository, Mockito.never()).deleteById(Mockito.any());
    }

    // ======================== findByUsuario ========================

    @Test
    public void deveRetornarTarefasPorUsuario() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        // findByUsuario() agora valida se o usuário existe — precisa estar mockado
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(tarefaRepository.findByUsuarioId(1L)).thenReturn(List.of(tarefa));
        Mockito.when(modelMapper.map(tarefa, TarefaResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        List<TarefaResponseDTO> resultado = tarefaService.findByUsuario(1L);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdUsuario());

        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaRepository, Mockito.times(1)).findByUsuarioId(1L);
    }

    @Test
    public void deveLancarExcecaoAoBuscarTarefasDeUsuarioInexistente() {
        // --- ARRANGE ---
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.findByUsuario(99L));

        // Usuário não encontrado = não deve nem consultar as tarefas.
        Mockito.verify(tarefaRepository, Mockito.never()).findByUsuarioId(Mockito.any());
    }

    // ======================== atualizarStatus ========================

    @Test
    public void deveAtualizarStatusDaTarefa() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        Tarefa tarefaSalva = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.CONCLUIDA, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.CONCLUIDA, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        Mockito.when(tarefaRepository.save(tarefa)).thenReturn(tarefaSalva);
        Mockito.when(modelMapper.map(tarefaSalva, TarefaResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.atualizarStatus(1L, StatusTarefa.CONCLUIDA);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(StatusTarefa.CONCLUIDA, resultado.getStatus());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefa);
    }

    @Test
    public void deveLancarExcecaoAoAtualizarStatusDeTarefaNaoEncontrada() {
        // --- ARRANGE ---
        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.atualizarStatus(99L, StatusTarefa.CONCLUIDA));

        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }
}