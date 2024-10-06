package fall24.swp391.g1se1868.koiauction.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customizeOpenAPI() {

        List<Server> servers = new ArrayList<>();
        final String securitySchemeName = "bearerAuth";
        servers.add(new Server().url("koi-auction-backend-dwe7hvbuhsdtgafe.southeastasia-01.azurewebsites.net"));

        return new OpenAPI()
                .servers(servers)
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}

