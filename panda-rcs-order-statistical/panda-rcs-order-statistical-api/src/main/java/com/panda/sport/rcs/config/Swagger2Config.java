package com.panda.sport.rcs.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * http://localhost:8080/doc.html
 * @author lithan
 */
@Configuration
@EnableSwagger2
public class Swagger2Config extends WebMvcConfigurationSupport {
    /**
     * 日志管理
     */
    private Logger log = LoggerFactory.getLogger(Swagger2Config.class);

	@Value("${swagger.is.enable:true}")
	private boolean isSwaggerEnabled;
    @Bean
    public Docket buildRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
        		.enable(isSwaggerEnabled)
                .apiInfo(buidApiInfo())
                .select()
                //.apis(RequestHandlerSelectors.any())
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage("com.panda.sport.rcs.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo buidApiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("风控保护伞API文档")
                //描述
                .description("稳")
                //版本号
                .version("2.0")
                //创建人
                .contact(new Contact("lithan", "youtube.com", "lithan@qq.com"))
                .termsOfServiceUrl("http://www.github.com")
                .license("Apache LICENSE")
                .licenseUrl("http://www.gitee.com")
                .build();
    }

    /**
     * 防止@EnableMvc把默认的静态资源路径覆盖了，手动设置的方式
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/").setCachePeriod(0);

    }

}