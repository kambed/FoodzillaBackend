package pl.better.foodzillabackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class FoodzillaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodzillaBackendApplication.class, args);
    }

}
