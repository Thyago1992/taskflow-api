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

/*
 * @ExtendWith(MockitoExtension.class)
 * Ativa o Mockito nesta classe de teste.
 * Sem isso, as anotações @Mock e @InjectMocks não funcionam.
 * Importante: NÃO sobe o contexto do Spring — os testes são rápidos e isolados.
 */
@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    /*
     * @InjectMocks
     * Cria uma instância REAL do TarefaService e injeta os mocks declarados abaixo.
     * É o objeto que estamos testando de verdade.
     */
    @InjectMocks
    private TarefaService tarefaService;

    /*
     * @Mock
     * Cria uma versão FALSA do TarefaRepository.
     * Ela não acessa o banco de dados — apenas simula o comportamento que definirmos.
     * Toda a dependência do service precisa ter um @Mock correspondente aqui.
     */
    @Mock
    private TarefaRepository tarefaRepository;

    /*
     * @Mock
     * Versão FALSA do UsuarioRepository.
     * O TarefaService usa o usuarioRepository no método save()
     * para buscar o usuario pelo id antes de criar a tarefa.
     * Sem este mock, o Mockito não conseguiria injetar todas as dependências do service.
     */
    @Mock
    private UsuarioRepository usuarioRepository;

    /*
     * @Mock
     * Versão FALSA do ModelMapper.
     * Sem este mock, o modelMapper seria null dentro do service, causando NullPointerException.
     */
    @Mock
    private ModelMapper modelMapper;

    // ======================== findAll ========================

    @Test
    public void deveRetornarTodasTarefas() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        // Simulamos o repositório retornando uma lista com uma tarefa.
        Mockito.when(tarefaRepository.findAll()).thenReturn(List.of(tarefa));

        // Simulamos o mapper convertendo a entidade Tarefa para TarefaResponseDTO.
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

        // Optional.of(tarefa) = registro encontrado no banco com id=1.
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
        // Optional.empty() = nenhum registro encontrado com id=99.
        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        // O service deve lançar ObjectNotFoundException por causa do orElseThrow().
        // Se não lançar, o teste falha.
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

        // O service busca o usuario pelo id antes de criar a tarefa.
        // Aqui simulamos que o usuario existe.
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Mockito.any(Tarefa.class) significa "qualquer objeto do tipo Tarefa".
        // Usamos isso porque o service cria a Tarefa internamente com new Tarefa(),
        // então não temos a referência exata do objeto para comparar.
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

        // O usuario com id=99 não existe — o service deve lançar exceção.
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.save(request));

        // Se o usuario não foi encontrado, a tarefa não deve ser salva.
        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }

    // ======================== update ========================

    @Test
    public void deveAtualizarTarefa() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa Atualizada", "Nova descrição", StatusTarefa.EM_ANDAMENTO, PrioridadeTarefa.MEDIA, LocalDate.now().plusDays(10), 1L);

        // Estado ANTES da atualização.
        Tarefa tarefaExistente = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);

        // Estado DEPOIS da atualização (retornado pelo save).
        Tarefa tarefaSalva = new Tarefa(1L, "Tarefa Atualizada", "Nova descrição", StatusTarefa.EM_ANDAMENTO, PrioridadeTarefa.MEDIA, LocalDate.now(), LocalDate.now().plusDays(10), usuario);

        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa Atualizada", "Nova descrição", StatusTarefa.EM_ANDAMENTO, PrioridadeTarefa.MEDIA, LocalDate.now(), LocalDate.now().plusDays(10), 1L);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefaExistente));

        // O service altera os campos da tarefaExistente diretamente e a passa para o save.
        // Por isso usamos tarefaExistente no save, não tarefaSalva.
        Mockito.when(tarefaRepository.save(tarefaExistente)).thenReturn(tarefaSalva);
        Mockito.when(modelMapper.map(tarefaSalva, TarefaResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.update(1L, request);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals("Tarefa Atualizada", resultado.getTitulo());
        assertEquals(StatusTarefa.EM_ANDAMENTO, resultado.getStatus());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(tarefaRepository, Mockito.times(1)).save(tarefaExistente);
    }

    @Test
    public void deveLancarExcecaoAoAtualizarTarefaNaoEncontrada() {
        // --- ARRANGE ---
        TarefaRequestDTO request = new TarefaRequestDTO("Tarefa", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now().plusDays(7), 1L);
        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.update(99L, request));

        // Tarefa não encontrada = save não pode ser chamado.
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
        // delete é void — não retorna nada. O verify é a única forma de confirmar
        // que o comportamento foi correto.
        tarefaService.delete(1L);

        // --- ASSERT ---
        // O service deve primeiro buscar a tarefa (para validar que existe)...
        Mockito.verify(tarefaRepository, Mockito.times(1)).findById(1L);

        // ...e só depois deletar.
        Mockito.verify(tarefaRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void deveLancarExcecaoAoDeletarTarefaNaoEncontrada() {
        // --- ARRANGE ---
        Mockito.when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> tarefaService.delete(99L));

        // Se a tarefa não existe, o deleteById jamais deve ser chamado.
        Mockito.verify(tarefaRepository, Mockito.never()).deleteById(Mockito.any());
    }

    // ======================== findByUsuario ========================

    @Test
    public void deveRetornarTarefasPorUsuario() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);
        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        // findByUsuarioId é um método customizado do repositório que filtra
        // tarefas pelo id do usuario dono delas.
        Mockito.when(tarefaRepository.findByUsuarioId(1L)).thenReturn(List.of(tarefa));
        Mockito.when(modelMapper.map(tarefa, TarefaResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        List<TarefaResponseDTO> resultado = tarefaService.findByUsuario(1L);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdUsuario());

        Mockito.verify(tarefaRepository, Mockito.times(1)).findByUsuarioId(1L);
    }

    // ======================== atualizarStatus ========================

    @Test
    public void deveAtualizarStatusDaTarefa() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        // Tarefa com status inicial PENDENTE.
        Tarefa tarefa = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.PENDENTE, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);

        // Tarefa após o status ser alterado para CONCLUIDA.
        Tarefa tarefaSalva = new Tarefa(1L, "Tarefa 1", "Descrição", StatusTarefa.CONCLUIDA, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), usuario);

        TarefaResponseDTO dto = new TarefaResponseDTO(1L, "Tarefa 1", "Descrição", StatusTarefa.CONCLUIDA, PrioridadeTarefa.ALTA, LocalDate.now(), LocalDate.now().plusDays(7), 1L);

        Mockito.when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        // O service altera o status da tarefa existente e salva o mesmo objeto.
        // Por isso o save recebe tarefa (não tarefaSalva).
        Mockito.when(tarefaRepository.save(tarefa)).thenReturn(tarefaSalva);
        Mockito.when(modelMapper.map(tarefaSalva, TarefaResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        TarefaResponseDTO resultado = tarefaService.atualizarStatus(1L, StatusTarefa.CONCLUIDA);

        // --- ASSERT ---
        assertNotNull(resultado);

        // O status retornado deve ser CONCLUIDA, não mais PENDENTE.
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

        // Tarefa não encontrada = save não pode ser chamado.
        Mockito.verify(tarefaRepository, Mockito.never()).save(Mockito.any());
    }
}