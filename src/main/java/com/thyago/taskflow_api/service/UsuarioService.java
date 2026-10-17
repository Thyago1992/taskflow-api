package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.dto.UsuarioRequestDTO;
import com.thyago.taskflow_api.dto.UsuarioResponseDTO;
import com.thyago.taskflow_api.entity.Usuario;
import com.thyago.taskflow_api.exception.EmailAlreadyExistsException;
import com.thyago.taskflow_api.exception.ObjectNotFoundException;
import com.thyago.taskflow_api.exception.UsuarioPossuiTarefasException;
import com.thyago.taskflow_api.mapper.UsuarioMapper;
import com.thyago.taskflow_api.repository.TarefaRepository;
import com.thyago.taskflow_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TarefaRepository tarefaRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          TarefaRepository tarefaRepository,
                          UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.tarefaRepository = tarefaRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.MSG_USUARIO_NAO_ENCONTRADO));
        return usuarioMapper.toResponse(usuario);
    }

    public UsuarioResponseDTO save(UsuarioRequestDTO dto) {
        String email = normalizar(dto.getEmail());
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(email);
        Usuario salvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(salvo);
    }

    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.MSG_USUARIO_NAO_ENCONTRADO));

        String email = normalizar(dto.getEmail());
        if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new EmailAlreadyExistsException();
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(email);
        Usuario salvo = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(salvo);
    }

    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ObjectNotFoundException(ObjectNotFoundException.MSG_USUARIO_NAO_ENCONTRADO);
        }
        if (tarefaRepository.existsByUsuarioId(id)) {
            throw new UsuarioPossuiTarefasException();
        }
        usuarioRepository.deleteById(id);
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase();
    }

}