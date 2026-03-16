package com.thyago.taskflow_api.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
