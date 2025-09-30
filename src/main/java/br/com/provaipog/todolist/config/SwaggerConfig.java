package br.com.provaipog.todolist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TodoList API")
                        .description("API para gerenciamento de tarefas")
                        .version("1.0.0")
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server().url("http://localhost:8080").description("Servidor de desenvolvimento"));
    }
}
