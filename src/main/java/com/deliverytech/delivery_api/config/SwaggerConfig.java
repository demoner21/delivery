package com.deliverytech.delivery_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                /* 
                 Configurações de informações globais da API. 
                 Estes dados aparecerão no cabeçalho da página de documentação do Swagger UI.
                */
                .info(new Info()
                        /* Define o título da aplicação na documentação */
                        .title("DeliveryTech API")
                        /* Define a versão atual da API */
                        .version("1.0.0")
                        /* Breve descrição do propósito e das funcionalidades da API */
                        .description("API REST completa para plataforma de delivery")
                        
                        /* 
                         Define as informações de contato da equipe responsável pela API,
                         útil para que os consumidores saibam a quem recorrer em caso de dúvidas.
                        */
                        .contact(new Contact()
                                .name("Equipe DeliveryTech")
                                .email("dev@deliverytech.com")
                                .url("https://deliverytech.com"))
                        
                        /* 
                         Define a licença de uso e distribuição da API,
                         essencial para definir os limites jurídicos de uso por terceiros.
                        */
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                
                /* 
                 Define a lista de servidores/ambientes onde a API está hospedada.
                 Permite que o usuário teste as rotas diretamente do Swagger UI 
                 alternando entre ambiente local, homologação ou produção.
                */
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.deliverytech.com")
                                .description("Servidor de Produção")
                ));
    }
}
