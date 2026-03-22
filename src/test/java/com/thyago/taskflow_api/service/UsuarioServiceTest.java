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

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    // ======================== findAll ========================

    @Test
    void deveRetornarTodosUsuarios() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        Mockito.when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        Mockito.when(modelMapper.map(usuario, UsuarioResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        List<UsuarioResponseDTO> usuarios = usuarioService.findAll();

        // --- ASSERT ---
        assertNotNull(usuarios);
        assertEquals(1, usuarios.size());
        assertEquals("Carol", usuarios.get(0).getNome());

        Mockito.verify(usuarioRepository, Mockito.times(1)).findAll();
    }

    // ======================== findById ========================

    @Test
    void deveRetornarUsuarioPorId() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

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
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // --- ARRANGE ---
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> usuarioService.findById(99L));

        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(99L);
    }

    // ======================== save ========================

    @Test
    void deveSalvarUsuario() {
        // --- ARRANGE ---
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol", "carol@gmail.com");
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        Mockito.when(usuarioRepository.existsByEmail("carol@gmail.com")).thenReturn(false);
        Mockito.when(modelMapper.map(request, Usuario.class)).thenReturn(usuario);
        Mockito.when(usuarioRepository.save(usuario)).thenReturn(usuario);
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
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        // --- ARRANGE ---
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol", "carol@gmail.com");

        Mockito.when(usuarioRepository.existsByEmail("carol@gmail.com")).thenReturn(true);

        // --- ACT + ASSERT ---
        assertThrows(EmailAlreadyExistsException.class, () -> usuarioService.save(request));

        Mockito.verify(usuarioRepository, Mockito.times(1)).existsByEmail("carol@gmail.com");
        Mockito.verify(usuarioRepository, Mockito.never()).save(Mockito.any());
    }

    // ======================== update ========================

    @Test
    void deveAtualizarUsuario() {
        // --- ARRANGE ---
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol Atualizada", "carol.nova@gmail.com");
        Usuario usuarioExistente = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Usuario usuarioSalvo = new Usuario(1L, "Carol Atualizada", "carol.nova@gmail.com", LocalDateTime.now());
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Carol Atualizada", "carol.nova@gmail.com", LocalDateTime.now());

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        // Email mudou, então verifica se o novo já pertence a outro usuário.
        Mockito.when(usuarioRepository.existsByEmail("carol.nova@gmail.com")).thenReturn(false);
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
    void deveAtualizarUsuarioSemMudarEmail() {
        // --- ARRANGE ---
        // Quando o email não muda, a verificação de duplicidade não deve ser chamada.
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol Atualizada", "carol@gmail.com");
        Usuario usuarioExistente = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());
        Usuario usuarioSalvo = new Usuario(1L, "Carol Atualizada", "carol@gmail.com", LocalDateTime.now());
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Carol Atualizada", "carol@gmail.com", LocalDateTime.now());

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        Mockito.when(usuarioRepository.save(usuarioExistente)).thenReturn(usuarioSalvo);
        Mockito.when(modelMapper.map(usuarioSalvo, UsuarioResponseDTO.class)).thenReturn(dto);

        // --- ACT ---
        UsuarioResponseDTO resultado = usuarioService.update(1L, request);

        // --- ASSERT ---
        assertNotNull(resultado);
        assertEquals("Carol Atualizada", resultado.getNome());

        // Email igual ao atual — existsByEmail não deve ser chamado.
        Mockito.verify(usuarioRepository, Mockito.never()).existsByEmail(Mockito.any());
        Mockito.verify(usuarioRepository, Mockito.times(1)).save(usuarioExistente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarComEmailDeOutroUsuario() {
        // --- ARRANGE ---
        // Carol tenta mudar o seu email para um que já pertence a outro usuário.
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol", "outro@gmail.com");
        Usuario usuarioExistente = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        Mockito.when(usuarioRepository.existsByEmail("outro@gmail.com")).thenReturn(true);

        // --- ACT + ASSERT ---
        assertThrows(EmailAlreadyExistsException.class, () -> usuarioService.update(1L, request));

        Mockito.verify(usuarioRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioNaoEncontrado() {
        // --- ARRANGE ---
        UsuarioRequestDTO request = new UsuarioRequestDTO("Carol", "carol@gmail.com");

        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> usuarioService.update(99L, request));

        Mockito.verify(usuarioRepository, Mockito.never()).save(Mockito.any());
    }

    // ======================== delete ========================

    @Test
    void deveDeletarUsuario() {
        // --- ARRANGE ---
        Usuario usuario = new Usuario(1L, "Carol", "carol@gmail.com", LocalDateTime.now());

        Mockito.when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // --- ACT ---
        usuarioService.delete(1L);

        // --- ASSERT ---
        Mockito.verify(usuarioRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(usuarioRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarUsuarioNaoEncontrado() {
        // --- ARRANGE ---
        Mockito.when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT + ASSERT ---
        assertThrows(ObjectNotFoundException.class, () -> usuarioService.delete(99L));

        Mockito.verify(usuarioRepository, Mockito.never()).deleteById(Mockito.any());
    }
}