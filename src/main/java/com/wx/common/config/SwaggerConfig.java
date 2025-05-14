//package com.wx.common.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//
//    /**
//     * 创建 API 应用
//     * apiInfo 增加 API 相关信息
//     * 通过 select() 函数返回一个 ApiSelectorBuilder 实例，用来控制哪些接口暴露给 Swagger 来展现
//     * 本例采用指定扫描的包路径来定义指定要建立 API 的目录
//     */
//    @Bean
//    public Docket createRestApi(){
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo()) // 用来展示该 API 的基本信息
//                .select()   // 返回一个 ApiSelectorBuilder 实例，用来控制哪些接口暴露给 Swagger 来展现
////                .apis(RequestHandlerSelectors.basePackage("com.wx"))   // 配置包扫描路径（根据自己项目调整，通常配置为控制器路径）
//                .paths(PathSelectors.any()) //
//                .build();
//    }
//
//    /**
//     * 创建 API 的基本信息（这些基本信息会展现在文档页面中）
//     * 访问地址：http://xxx/swagger-ui.html
//     */
//    private ApiInfo apiInfo(){
//        return new ApiInfoBuilder()
//                .title("RESTful APIs")
//                .description("RESTful APIs")
//                .version("1.0")
//                .build();
//    }
//}
