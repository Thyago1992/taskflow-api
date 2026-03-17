package com.thyago.taskflow_api.config;

import com.thyago.taskflow_api.dto.TarefaResponseDTO;
import com.thyago.taskflow_api.entity.Tarefa;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Tarefa.class, TarefaResponseDTO.class)
                .setPostConverter(context -> {
                    Tarefa source = context.getSource();
                    TarefaResponseDTO dest = context.getDestination();
                    if (source.getUsuario() != null) {
                        dest.setIdUsuario(source.getUsuario().getId());
                    }
                    return dest;
                });

        return modelMapper;
    }
}
