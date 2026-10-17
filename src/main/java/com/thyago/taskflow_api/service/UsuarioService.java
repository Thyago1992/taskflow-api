package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.dto.UsuarioRequestDTO;
import com.thyago.taskflow_api.dto.UsuarioResponseDTO;
import com.thyago.taskflow_api.entity.Usuario;
import com.thyago.taskflow_api.exception.EmailAlreadyExistsException;
import com.thyago.taskflow_api.exception.ObjectNotFoundException;
import com.thyago.taskflow_api.exception.UsuarioPossuiTarefasException;
import com.thyago.taskflow_api.repository.TarefaRepository;
import com.thyago.taskflow_api.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TarefaRepository tarefaRepository;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          TarefaRepository tarefaRepository,
                          ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
        this.tarefaRepository = tarefaRepository;
        this.modelMapper = modelMapper;
    }

    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioResponseDTO.class))
                .toList();
    }

    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(ObjectNotFoundException.MSG_USUARIO_NAO_ENCONTRADO));
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO save(UsuarioRequestDTO dto) {
        String email = normalizar(dto.getEmail());
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        usuario.setEmail(email);
        Usuario salvo = usuarioRepository.save(usuario);
        return modelMapper.map(salvo, UsuarioResponseDTO.class);
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
        return modelMapper.map(salvo, UsuarioResponseDTO.class);
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