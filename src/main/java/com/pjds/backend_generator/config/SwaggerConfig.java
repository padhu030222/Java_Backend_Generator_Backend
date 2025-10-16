//package com.pjds.config;
//
//import io.swagger.v3.oas.models.ExternalDocumentation;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.info.License;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SwaggerConfig {
//
//    @Bean
//    public OpenAPI customOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("ERP API")
//                        .description("API documentation for the ERP system")
//                        .version("1.0")
//                        .contact(new Contact()
//                                .name("Tech3 Support")
//                                .email("support@tech3.com")
//                                .url("https://tech3.com"))
//                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
//                .externalDocs(new ExternalDocumentation()
//                        .description("ERP Documentation")
//                        .url("https://tech3.com/docs"));
//    }
//}
