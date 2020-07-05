package com.pwc.config;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Date;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Bean
    protected Docket api() {
        TypeResolver typeResolver = new TypeResolver();
        final ResolvedType jsonNodeType = typeResolver.resolve(JsonNode.class);
        final ResolvedType stringType = typeResolver.resolve(String.class);
        new ParameterBuilder()
                .description("")
                .defaultValue("")
                .parameterType("body")
                .modelRef(new ModelRef("string")).build();
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("PWC API")
                // TODO: define swagger.yaml and yaml file for each Controller
                // quick fix for the time being
                .additionalModels(typeResolver.resolve(Duration.class))
                // .alternateTypeRules(new AlternateTypeRule(jsonNodeType, stringType))
                .directModelSubstitute(Timestamp.class, Date.class)
                .directModelSubstitute(Duration.class, String.class)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }

    @Bean
    protected ApiInfo apiInfo() {
        final ApiInfoBuilder builder = new ApiInfoBuilder();
        builder.title("PWC API");
        return builder.build();
    }

    @Bean
    protected Docket restApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("PWC API - OAuth 2")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/oauth/**"))
                .build();
    }

    @Bean
    protected ApiInfo restApiInfo() {
        final ApiInfoBuilder builder = new ApiInfoBuilder();
        builder.title("PWC API - OAuth 2");
        return builder.build();
    }
}