# Ribbon 负载均衡

之前已经实现了微服务的注册与发现, 服务消费者可以通过注册中心获取到服务提供者的信息, 但是这里还有一个问题, 就是负载均衡, 一般生产环境, 每个微服务都会有多个实例, 那么服务消费者如何将请求分摊到每个服务提供者实例上呢?

## 为服务消费者整合 Ribbon

1. 复制项目 microserver-customer-movie, 将 ArtifactId 修改为 microserver-customer-movie-ribbon

2. 为项目引入 ribbon 依赖, ribbon的依赖是

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-ribbon</artifactId>
</dependency>
```

但是之前已经引入了 `spring-cloud-starter-eureka`, 里面已经包含了 `spring-cloud-starter-ribbon`, 所以这里就不需要引入了

3. 为 RestTemplate 添加 @LoadBalanced 注解

```
@SpringBootApplication
@EnableDiscoveryClient
public class MicroserverConsumeMovieApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(MicroserverConsumeMovieApplication.class, args);
	}
}
```

4. 修改 MovieController
```
@RestController
public class MovieController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @GetMapping("/user/{id}")
    public User queryById(@PathVariable Long id) {
        return restTemplate.getForObject("http://microserver-provider-user/user/" + id, User.class);
    }

    @GetMapping("/log-instance")
    public String logUserInstance() {
        ServiceInstance serviceInstance = this.loadBalancerClient.choose("microserver-provider-user");
        LOGGER.info("{}:{}:{}", serviceInstance.getServiceId(), serviceInstance.getHost(), serviceInstance.getPort());
        return serviceInstance.getServiceId().concat(" : ")
                .concat(serviceInstance.getHost().concat(" : ")
                .concat(String.valueOf(serviceInstance.getPort())));
    }
}
```

5. 启动多个用户微服务

```
java -jar microserver-provider-user-0.0.1-SNAPSHOT.jar --server.port=8004
java -jar microserver-provider-user-0.0.1-SNAPSHOT.jar --server.port=8005
java -jar microserver-provider-user-0.0.1-SNAPSHOT.jar --server.port=8006
```

6. 查看打印日志
可以看到启动的三个势力是轮流访问的

```
2018-04-19 13:53:27.216  INFO 8140 --- [io-8002-exec-10] c.h.m.web.MovieController                : microserver-provider-user:192.168.20.234:8006
2018-04-19 13:53:27.380  INFO 8140 --- [nio-8002-exec-3] c.h.m.web.MovieController                : microserver-provider-user:192.168.20.234:8004
2018-04-19 13:53:27.547  INFO 8140 --- [nio-8002-exec-4] c.h.m.web.MovieController                : microserver-provider-user:192.168.20.234:8005
2018-04-19 13:53:27.851  INFO 8140 --- [nio-8002-exec-6] c.h.m.web.MovieController                : microserver-provider-user:192.168.20.234:8006
2018-04-19 13:53:28.028  INFO 8140 --- [nio-8002-exec-8] c.h.m.web.MovieController                : microserver-provider-user:192.168.20.234:8004
2018-04-19 13:53:28.181  INFO 8140 --- [io-8002-exec-10] c.h.m.web.MovieController                : microserver-provider-user:192.168.20.234:8005
2018-04-19 13:53:28.331  INFO 8140 --- [nio-8002-exec-3] c.h.m.web.MovieController                : microserver-provider-user:192.168.20.234:8006
2018-04-19 13:53:28.474  INFO 8140 --- [nio-8002-exec-4] c.h.m.web.MovieController                : microserver-provider-user:192.168.20.234:8004
2018-04-19 13:53:28.602  INFO 8140 --- [nio-8002-exec-6] c.h.m.web.MovieController                : microserver-provider-user:192.168.20.234:8005
```

## 服务消费者自定义 Ribbon 配置 (java 代码)
本例是一个随机的负载均衡策略
1. 复制 microserver-consumer-movie-ribbon, 修改 ArtifactId 为 microserver-consumer-movie-ribbon-customizing
2. 创建Ribbon配置类

    ```
    @Configuration
    public class RibbonConfiguration {
      @Bean
      public IRule ribbonRule() {
          // 负载均衡规则为随机
          return new RandomRule();
      }
    }
    ```

3. 创建空类, 并添加配置信息

    ```
    @Configuration
    @RibbonClient(name = "microserver-provider-user", configuration = RibbonConfiguration.class)
    public class TestRibbon {
    }
    ```

## 服务消费者自定义 Ribbon 配置(通过配置)

本例是一个随机的负载均衡策略

1. 复制 microserver-consumer-movie-ribbon, 修改 ArtifactId 为 microserver-consumer-movie-ribbon-customizing-properties
2. 添加配置信息

```
microserver-provider-user:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```

这样的效果和通过java代码配置的一模一样

## 脱离 Eureka 使用 Ribbon
适用情况: 一些遗留的微服务, 可能没有注册到 Eureka; 或者根本不是用 spring cloud 开发.
脱离 Eureaka 后, 就需要将服务提供者的实例的 ip 和端口在 ribbon 信息中写全. 如果多了一个实例, 那么就需要将配置信息修改一下.

1. 复制一份 microserver-consumer-movie-ribbon, 修改 ArtifactId 为 microserver-consumer-movie-without-eureka
2. 修改依赖, 将 eureka 的依赖修改为 Ribbon 的依赖
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```
3. 添加配置信息
```
microserver-provider-user:
  ribbon:
    listOfServers: localhost:8000,localhost:8001
```
4. 启动服务提供者-user
```
java -jar microserver-provider-user-0.0.1-SNAPSHOT.jar --server.port=8000
java -jar microserver-provider-user-0.0.1-SNAPSHOT.jar --server.port=8001
```
5. 查看日志(访问: localhost:8002/log-instance)
```
2018-04-19 18:47:15.077  INFO 11360 --- [nio-8002-exec-8] c.h.m.web.MovieController                : microserver-provider-user:localhost:8000
2018-04-19 18:47:15.246  INFO 11360 --- [nio-8002-exec-9] c.h.m.web.MovieController                : microserver-provider-user:localhost:8001
2018-04-19 18:47:15.414  INFO 11360 --- [nio-8002-exec-2] c.h.m.web.MovieController                : microserver-provider-user:localhost:8000
2018-04-19 18:47:15.587  INFO 11360 --- [nio-8002-exec-4] c.h.m.web.MovieController                : microserver-provider-user:localhost:8001
2018-04-19 18:47:15.747  INFO 11360 --- [nio-8002-exec-6] c.h.m.web.MovieController                : microserver-provider-user:localhost:8000
2018-04-19 18:47:15.916  INFO 11360 --- [nio-8002-exec-8] c.h.m.web.MovieController                : microserver-provider-user:localhost:8001
2018-04-19 18:47:16.067  INFO 11360 --- [nio-8002-exec-9] c.h.m.web.MovieController                : microserver-provider-user:localhost:8000
2018-04-19 18:47:16.218  INFO 11360 --- [nio-8002-exec-2] c.h.m.web.MovieController                : microserver-provider-user:localhost:8001
```
