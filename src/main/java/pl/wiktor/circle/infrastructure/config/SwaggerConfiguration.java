package pl.wiktor.circle.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.function.Predicate;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket postsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo()).select().paths(apiPaths()).build();
    }

    private Predicate<String> apiPaths() {
        return PathSelectors.ant("/contestant/**").or(PathSelectors.ant("/console"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Circle backend API")
                .description("Circle backend API reference for connection")
                .contact(new Contact("Wiktor Metelski", null, "metelski.wiktor@gmail.com"))
                .version("1.0").build();
    }
}
