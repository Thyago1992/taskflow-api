package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.dto.UsuarioRequestDTO;
import com.thyago.taskflow_api.dto.UsuarioResponseDTO;
import com.thyago.taskflow_api.entity.Usuario;
import com.thyago.taskflow_api.exception.EmailAlreadyExistsException;
import com.thyago.taskflow_api.exception.ObjectNotFoundException;
import com.thyago.taskflow_api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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
class UsuarioServiceTest {

    /*
     * @InjectMocks
     * Cria uma instância REAL do UsuarioService e injeta os mocks declarados abaixo.
     * É o objeto que estamos testando de verdade.
     */
    @InjectMocks
    private UsuarioService usuarioService;

    /*
     * @Mock
     * Cria uma versão FALSA do UsuarioRepository.
     * Ela não acessa o banco de dados — apenas simula o comportamento que definirmos.
     * Toda dependência do service precisa ter um @Mock correspondente aqui.
     */
    @Mock
    private UsuarioRepository usuarioRepository;

    /*
     * @Mock
     * Versão falsa do ModelMapper.
     * Sem isso, o ModelMapper seria null dentro do service, causando NullPointerException.
     */
    @Mock
    private ModelMapper modelMapper;

    // ======================== findAll ========================

    @Test
    public void deveRetornarTodosUsuarios() {
        // --- ARRANGE (Preparar) ---
        // Criamos os objetos que serão usados no teste.
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        // Mockito.when(...).thenReturn(...)
        // "Quando o repositório chamar findAll(), retorne esta lista."
        // O repositório FALSO não vai ao banco — apenas devolve o que mandamos aqui.
        Mockito.when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        // "Quando o mapper converter este usuario para UsuarioResponseDTO, retorne este dto."
        // Sem isso, o mapper retornaria null (comportamento padrão dos mocks).
        Mockito.when(modelMapper.map(usuario, UsuarioResponseDTO.class)).thenReturn(dto);

        // --- ACT (Agir) ---
        // Chamamos o método REAL do service que queremos testar.
        List<UsuarioResponseDTO> usuarios = usuarioService.findAll();

        // --- ASSERT (Verificar) ---
        // Confirmamos que o resultado é o esperado.
        assertNotNull(usuarios);                          // A lista não pode ser null
        assertEquals(1, usuarios.size());                 // Deve ter exatamente 1 elemento
        assertEquals("Carol", usuarios.get(0).getNome()); // O nome deve ser "Carol"

        // Mockito.verify(...)
        // Confirma que o método findAll() foi chamado exatamente 1 vez no repositório.
        // Garante que o service realmente delegou a busca ao repositório.
        Mockito.verify(usuarioRepository, Mockito.times(1)).findAll();
    }

    // ======================== findById ========================

    @Test
    public void deveRetornarUsuarioPorId() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        // Optional.of(usuario) simula um banco que encontrou o registro com id=1.
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        Mockito.when(modelMapper.map(usuario, UsuarioResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        UsuarioResponseDTO resultado = usuarioService.findById(1L);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Carol", resultado.getNome());
        assertEquals("carol@gmail.com", resultado.getEmail());

        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // --- ARRANGE ---
        // Optional.empty() simula um banco que NÃO encontrou nenhum registro com id=99.
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        // assertThrows verifica que o método lança a exceção esperada.
        // Se o service NÃO lançar ObjectNotFoundException, o teste FALHA.
        assertThrows(ObjectNotFoundException.class, () -> usuarioService.findById(99L));

        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(99L);
    }

    // ======================== save ========================

    @Test
    public void deveSalvarUsuario() {
        // --- ARRANGE ---
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol", "carol@gmail.com");
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        // "false" = email ainda não existe no banco, pode salvar.
        Mockito.when(usuarioRepository.existsByEmail("carol@gmail.com")).thenReturn(false);

        // O mapper converte o DTO de entrada em entidade.
        Mockito.when(modelMapper.map(request, Usuario.class)).thenReturn(usuario);

        // O repositório salva e retorna o mesmo usuario (com id gerado).
        Mockito.when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // O mapper converte a entidade salva para DTO de saída.
        Mockito.when(modelMapper.map(usuario, UsuarioResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        UsuarioResponseDTO resultado = usuarioService.save(request);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals("Carol", resultado.getNome());
        assertEquals("carol@gmail.com", resultado.getEmail());

        Mockito.verify(usuarioRepository, Mockito.times(1)).existsByEmail("carol@gmail.com");
        Mockito.verify(usuarioRepository, Mockito.times(1)).save(usuario);
    }

    @Test
    public void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // --- ARRANGE ---
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol", "carol@gmail.com");

        // "true" = email já existe no banco, deve lançar exceção.
        Mockito.when(usuarioRepository.existsByEmail("carol@gmail.com")).thenReturn(true);

        // --- ACT + ASSERT ---
        assertThrows(EmailAlreadyExistsException.class, () -> usuarioService.save(request));

        Mockito.verify(usuarioRepository, Mockito.times(1)).existsByEmail("carol@gmail.com");

        // Mockito.never() garante que o save NUNCA foi chamado.
        // Se o email já existe, não faz sentido tentar salvar.
        // Mockito.any() significa "qualquer objeto" — independente do valor.
        Mockito.verify(usuarioRepository, Mockito.never()).save(Mockito.any());
    }

    // ======================== update ========================

    @Test
    public void deveAtualizarUsuario() {
        // --- ARRANGE ---
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol Atualizada", "carol.nova@gmail.com");

        // Estado ANTES da atualização.
        Usuario usuarioExistente = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        // Estado DEPOIS da atualização (o que o repositório retorna após salvar).
        Usuario usuarioSalvo = new Usuario(1L, "Carol Atualizada", "carol.nova@gmail.com", LocalDateTime.now());

        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Carol Atualizada", "carol.nova@gmail.com", LocalDateTime.now());

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));

        // O service altera os campos do usuarioExistente e passa ele mesmo para o save.
        // Por isso o verify abaixo usa usuarioExistente, não usuarioSalvo.
        Mockito.when(usuarioRepository.save(usuarioExistente)).thenReturn(usuarioSalvo);
        Mockito.when(modelMapper.map(usuarioSalvo, UsuarioResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        UsuarioResponseDTO resultado = usuarioService.update(1L, request);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals("Carol Atualizada", resultado.getNome());
        assertEquals("carol.nova@gmail.com", resultado.getEmail());

        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(usuarioRepository, Mockito.times(1)).save(usuarioExistente);
    }

    @Test
    public void deveLancarExcecaoAoAtualizarUsuarioNaoEncontrado() {
        // --- ARRANGE ---
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol", "carol@gmail.com");
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> usuarioService.update(99L, request));

        // Se o usuario não existe, o save jamais deve ser chamado.
        Mockito.verify(usuarioRepository, Mockito.never()).save(Mockito.any());
    }

    // ======================== delete ========================

    @Test
    public void deveDeletarUsuario() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // --- ACT ---
        // O método delete não retorna nada (void), então não há resultado para capturar.
        // A única forma de verificar o comportamento é com o verify abaixo.
        usuarioService.delete(1L);

        // --- ASSERT ---
        // Confirmamos que o service buscou o usuario antes de deletar.
        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);

        // Confirmamos que o deleteById foi chamado com o id correto.
        Mockito.verify(usuarioRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void deveLancarExcecaoAoDeletarUsuarioNaoEncontrado() {
        // --- ARRANGE ---
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> usuarioService.delete(99L));

        // Se o usuario não existe, não deve tentar deletar nada.
        Mockito.verify(usuarioRepository, Mockito.never()).deleteById(Mockito.any());
    }
}