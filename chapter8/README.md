# Sring cloud Config 同一管理微服务配置
在微服务架构中, 微服务的配置管理一般有如下需求:
- 集中管理配置. 一个微服务架构的应用系统可能有成百上千个微服务, 因此集中管理配置是非常必要的.
- 不同的环境不同的配置. 例如数据源配置在不同的环境(开发, 测试, 预发布, 生产)中是不同的.
- 运行期间可动态调整. 例如根据各个微服务的负载情况, 动态调整数据源连接池大小或熔断阈值, 并且在调整配置时不停止微服务.
- 配置修改后可自动更新. 如配置内容发生变化, 微服务能够自动配置更新.

## 编写 Config Server

在 git 仓库新建一个分支 config-label-v1.0

- 在 git 仓库中新建几个配置文件
	- microserver-foo-dev.properties
	- microserver-foo-production.properties
	- microserver-foo-test.properties
	- microserver-foo.properties
	内容分别为:
	- profile=dev-1.0
	- profile=production-1.0
	- profile=test-1.0
	- profile=default-1.0
- 新建 maven 项目, ArtifactId 是 spring-cloud-config-server, 依赖如下

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

- 在启动类上添加注解

```
@SpringBootApplication
@EnableConfigServer
public class MicroserverConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserverConfigServerApplication.class, args);
	}
}

```

- 编写配置文件 application.yml

```
server:
  port: 8080
spring:
  application:
    name: microserver-config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/lvguoqiang/spring-cloud
```

- 测试

1. 访问 http://localhost:8080/config-label-v1.0/microserver-foo.properties
`profile: default-1.0`
2. 访问 http://localhost:8080/config-label-v1.0/microserver-foo-dev.properties
`profile: dev-1.0`
3. 访问 http://localhost:8080/microserver-foo/dev/config-label-v1.0

```
{
    "name":"microserver-foo",
    "profiles":[
        "dev"
    ],
    "label":"config-label-v1.0",
    "version":"022ff590aa46eed1b717b19ffce4bf859080c573",
    "state":null,
    "propertySources":[
        {
            "name":"https://github.com/lvguoqiang/spring-cloud/microserver-foo-dev.properties",
            "source":{
                "profile":"dev-1.0"
            }
        },
        {
            "name":"https://github.com/lvguoqiang/spring-cloud/microserver-foo.properties",
            "source":{
                "profile":"default-1.0"
            }
        }
    ]
}
```

4. 访问http://localhost:8080/config-label-v1.0/microserver-foo.yml
`profile: default-1.0`

- Config Server 端点

```
/{application}/{profile}[/label]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```
{application} 表示微服务的名称, 比如这里就是 microserver-foo
{label} 代表分支, 比如这里是 config-label-v1.0, 默认是 master 

## 编写 config client

- 新建 Maven 项目, ArtifactId 为 microserver-config-client, 依赖如下

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

- 编写配置文件 application.yml

```
server:
  port: 8081
```

- 编写配置文件 bootstrap.yml

```
spring:
  application:
    name: microserver-foo
  cloud:
    config:
      uri: http://localhost:8080/
      profile: dev
      label: config-label-v1.0
```
配置信息详解:
	- spring.application.name: 对应 Config Server 所获取配置文件中的 {application}
	- spring.cloud.uri: 对应 Config Server 的地址, 默认是 http://localhost:8888
	- spring.cloud.profile: 对应 Config Server 所获取配置文件中的 {profile}
	- spring.cloud.label: 指定 Git 仓库的分支

- 编写 Controller

```
@RestController
public class ConfigClientController {

    @Value("${profile}")
    private String profile;

    @GetMapping("/profile")
    public String hello() {
        return this.profile;
    }
}
```

- 测试

1. 启动 microserver-config-server
2. 启动 microserver-config-client
3. 访问 http://localhost:8081/profile
`dev-1.0`