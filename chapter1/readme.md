# 服务提供者总述

这是一个简单提供者的 demo, 用于提供一个查询用户的服务
集成了 mybatis, flyway, swagger

## 集成 flyway

1. 配置 依赖

```
<dependency>
	<groupId>org.flywaydb</groupId>
	<artifactId>flyway-core</artifactId>
	<version>4.0</version>
</dependency>
<plugin>
	<groupId>org.flywaydb</groupId>
	<artifactId>flyway-maven-plugin</artifactId>
	<version>5.0.2</version>
	<configuration>
		<url>
			<![CDATA[jdbc:mysql://localhost:3306/cloud?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true]]>
		</url>
		<user>root</user>
		<password>123456</password>
		<locations>db/schema</locations>
	</configuration>
</plugin>
```

2. 在 application.yml 中配置

```
flyway:
  locations: db/schema,db/data
  baseline-on-migrate: true
```

## 集成swagger

1. 添加依赖

```
<dependency>
    <groupId>io.swagger</groupId>
    <artifactId>swagger-annotations</artifactId>
    <version>1.5.10</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```

2. 生成swaggger配置类

```
package com.hand.microserversimpleprovideruser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket swaggerConfig() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hand.microserversimpleprovideruser"))
                .build();
    }

    private ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("简单提供者API")
                .contact(new Contact("汉得", "访问地址", "联系方式"))
                .build();
    }
}

```

## 问题

1. mapper 文件怎么都扫描不到
我的配置如下

```
mybatis:
  type-aliases-package: com.hand.*.domain
  mapper-locations: classpath:com/hand/microserversimpleprovideruser/mapper/*.xml
```

解决方法:
在Application中添加

`@MapperScan(value = "com.hand.microserversimpleprovideruser.mapper")`

2. application.yml 中 flyway配置标红, 显示已过时
解决方法: 降低springboot 版本到1.5.5