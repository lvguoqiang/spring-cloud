# 简单服务消费者总述
这里是一个简单服务消费者, 它的功能就是通过 RestTemplate 来调用服务提供者提供的接口

## RestTemplate使用

1. Application 配置

```
@SpringBootApplication
public class MicroserverSimpleConsumerMovieApplication {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(MicroserverSimpleConsumerMovieApplication.class, args);
	}
}
```

2. 在 Controler 中注入

```
@Autowired
RestTemplate restTemplate;
```

3. 使用
第一个参数是url, 第二个参数是要返回的数值的类型
```
restTemplate.getForObject("http://localhost:8035/user/" + id, User.class);
```

## 整合 Actuator
1. 添加依赖

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

2. 配置显示信息

```
info:
  app:
    name: @project.artifactId@
    encoding: @project.build.sourceEncoding@
    java:
      source: @java.version@
      target: @java.version@
```

## 硬编码存在的问题

分析:
	到这里已经完成了一个服务提供者和一个服务消费者, 并且可以使用服务提供者调用服务消费者, 但是这么写代码好吗? 我们把服务提供者的 ip 和端口写死到了代码中, 一旦服务提供者出现了问题, 或者Ip或者端口改变了, 那么我们就需要去修改代码. 这样就很不方便.
传统方式:
	将服务提供者的url写到配置文件中
	
```
user:
	userServerUrl: http://localhost:8035/user/
代码变为:

@Value("user.userServerUrl")
String userServerUrl;

@GetMapping("/user/{id}")
public User queryById(@PathVariable Long id) {
    return restTemplate.getForObject(userServerUrl + id, User.class);
}
```

这样做还是有很多局限性:
	* 使用范围有局限性: 服务提供者的端口和IP修改之后, 还是得修改配置文件, 重新发布服务
	* 无法动态伸缩: 在生产环境, 每一个微服务都会有多个实例, 从而实现容错和负载均衡. 这里很明显做不到

## Actuator 问题

- health 信息显示不全

```
management:
  endpoint:
    health:
      show-details: always
```