package com.panda.sport.rcs.oddin.config;

import groovy.lang.MetaClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author :wiker
 * @Date: 2023-10 19:24
 **/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build()
                .ignoredParameterTypes(MetaClass.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("提供给电竞请求的服务")
                .description("提供给电竞的服务接口请求")
                .version("1.0")
                .build();
    }

}
