package com.thyago.taskflow_api.service;

import com.thyago.taskflow_api.dto.UsuarioRequestDTO;
import com.thyago.taskflow_api.dto.UsuarioResponseDTO;
import com.thyago.taskflow_api.entity.Usuario;
import com.thyago.taskflow_api.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> modelMapper.map(usuario, UsuarioResponseDTO.class))
                .toList();
    }

    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return modelMapper.map(usuario, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO save(UsuarioRequestDTO dto) {
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        usuario.setDataCriacao(LocalDateTime.now());
        Usuario salvo = usuarioRepository.save(usuario);
        return modelMapper.map(salvo, UsuarioResponseDTO.class);
    }

    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        usuario.setId(id);
        Usuario salvo = usuarioRepository.save(usuario);
        return modelMapper.map(salvo, UsuarioResponseDTO.class);
    }

    public void delete(Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuarioRepository.deleteById(id);
    }
}
