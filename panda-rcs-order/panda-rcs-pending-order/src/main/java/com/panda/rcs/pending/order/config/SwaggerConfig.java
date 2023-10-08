package com.panda.rcs.pending.order.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        ParameterBuilder userParam = new ParameterBuilder();
        ParameterBuilder appParam = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        userParam.name("user-id").description("用户id").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        appParam.name("app-id").description("appId").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(userParam.build());
        pars.add(appParam.build());
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .paths(PathSelectors.any()).build()
                .globalOperationParameters(pars)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("预约投注服务")
                .description("预约投注服务")
                .version("1.0")
                .build();
    }

}
