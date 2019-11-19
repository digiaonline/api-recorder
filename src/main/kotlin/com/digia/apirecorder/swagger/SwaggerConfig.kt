package com.digia.apirecorder.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.swagger2.annotations.EnableSwagger2
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket



@Configuration
@EnableSwagger2
class SwaggerConfig {
    @Bean
    fun api(): Docket {

        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.digia.apirecorder"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())
    }

    fun apiInfo() : ApiInfo {
        return ApiInfoBuilder()
            .title("Api recorder")
            .description("Records feeds periodically and replay them on demand")
            .contact(Contact("Pierre Leonard", "www.digia.com", "pierre.leonard@digia.com"))
            .build()
    }
}