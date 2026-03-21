package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.dto.UsuarioRequestDTO;
import com.thyago.taskflow_api.dto.UsuarioResponseDTO;
import com.thyago.taskflow_api.entity.Usuario;
import com.thyago.taskflow_api.exception.EmailAlreadyExistsException;
import com.thyago.taskflow_api.exception.ObjectNotFoundException;
import com.thyago.taskflow_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, ModelMapper modelMapper) {
        this.usuarioRepository = usuarioRepository;
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
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO save(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        Usuario salvo = usuarioRepository.save(usuario);
        return modelMapper.map(salvo, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));

        // Verifica se o email pertence a OUTRO usuário
        if (!usuario.getEmail().equals(dto.getEmail())
                && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        Usuario salvo = usuarioRepository.save(usuario);
        return modelMapper.map(salvo, UsuarioResponseDTO.class);
    }

    public void delete(Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));
        usuarioRepository.deleteById(id);
    }


}
