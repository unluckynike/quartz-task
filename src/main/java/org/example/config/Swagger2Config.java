package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/*
 * @Package org.example.config
 * @Author hailin
 * @Date 2023/9/11
 * @Description :接口文档 地址 http://ip:port/swagger-ui.html
 */

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = "org.example.controller")
public class Swagger2Config {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())//选择所有的API接口进行文档展示
                .apis(RequestHandlerSelectors.basePackage("org.example.controller"))//指定API接口所在的包路径
                //.paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("采集系统-定时任务调度模块接口文档")
                .description("采集系统-定时任务调度模块接口测试")
                .version("1.0")
                .contact(new Contact("hailin", "https://github.com/unluckynike/quartz-task", "2230432084@qq.com"))
                .termsOfServiceUrl("")
                .license("")
                .licenseUrl("")
                .build();
    }
}