package com.deliverytech.delivery_api.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();


        // configurações do ModelMapper para mapeamento de objetos
        mapper.getConfiguration()
                /* define a estratégia de correspondência como STRICT 
                 para garantir que apenas campos com nomes e tipos correspondentes 
                 sejam mapeados 
                */
                .setMatchingStrategy(MatchingStrategies.STRICT)
                /* define se a correspondência de campos está habilitada */
                .setFieldMatchingEnabled(true)
                /* define o nível de acesso aos campos 
                 pode ser PUBLIC, PRIVATE, PACKAGE, PROTECTED 
                 PUBLIC pode ser acessado de qualquer lugar, 
                 PRIVATE apenas dentro da classe,
                 PACKAGE apenas dentro do mesmo pacote,
                 PROTECTED apenas dentro da classe e subclasses.
                 segue a mesma lógica de encapsulamento do Java
                */
                .setFieldAccessLevel(org.modelmapper.config.
                    Configuration.AccessLevel.PRIVATE);

        return mapper;
    }
}

