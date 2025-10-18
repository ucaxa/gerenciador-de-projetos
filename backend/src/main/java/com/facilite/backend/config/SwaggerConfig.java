package com.facilite.backend.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI kanbanOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kanban API")
                        .description("API para gerenciamento de projetos Kanban")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Suporte Kanban")
                                .email("suporte@kanban.com")
                                .url("https://kanban.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}