# 使用 Hystrix 实现微服务的容错处理
> 如果服务提供者响应非常慢, 那么消费者对提供者的请求就得强制等待, 知道提供者响应超时. 在高负载的场景下, 如果不做任何处理, 那么这个问题就会导致服务消费者的资源耗竭, 甚至导致服务器的崩溃.

## 雪崩效应及容错处理

### 雪崩效应

微服务架构通常会包括多个服务层. 微服务之间通过网络进行通信, 从而支撑整个微服务. 因此微服务之间难免存在依赖. 我们将服务提供者不可用导致服务消费者不可用, 从而逐级放大的过程称为雪崩效应.

### 容错处理

为了防止雪崩效应, 那么我们就需要一个强大的容错机制. 该机制需要保证一下两点

- 网络超时

- 使用断路模式

如果某个服务的请求有大量的超时(常常说明该服务不可用). 再让新的请求去访问该服务就会变得没有意义. 只会无谓的消耗资源. 断路器可理解为对容易导致错误的操作的代理. 这种代理能够统计一段时间内调用失败的次数, 并决定是正常请求的依赖服务, 还是直接返回.

# Hystrix

Hystrix 是一个实现了超时机制和断路器模式的工具类库.

## Hystrix简介

Hystrix 是 Netflix 开源的一个延迟和容错库, 用于隔离远程系统, 服务或者第三方库, 防止级联失败. 从而提升系统的可用性和容错性. Hystrix 主要通过以下几点来实现延迟与容错.

- 包裹请求: 使用 HystrixCommand 包裹对依赖的调用逻辑, 每个命令在独立线程中执行.

- 跳闸机制: 当某服务的错误率超过一定阈值时, Hystrix 可以实现自动或手动跳闸, 停止请求该服务一段时间.

- 资源隔离: Hystrix 为每个依赖维护了一个小型的线程池(或者信号量). 如果该线程池已满, 发往该依赖的请求就立即被拒绝, 而不是排队等候, 从而加速判定失败.

- 监控: Hystrix 可以近乎实时地监控运行指标和配置的变化, 例如成功, 失败, 超时以及被拒绝的请求

- 回退机制: 当请求失败, 超时, 被拒绝, 或当断路器打开时, 执行回退逻辑. 回退逻辑可以有开发人员自行提供.

- 自我修复: 当断路器打开一段时间后, 会自动进入 "半开" 状态.

## 通用方式整合 Hystrix

- 复制项目 microserver-consume-movie-ribbon 修改 ArtifactId 为 microserver-consume-movie-ribbon-hystrix
- 添加依赖

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

- 修改启动类(添加依赖)

```
@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrix
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

- 修改 MovieController, 让其中的 queryById 具备容错能力

```
@RestController
public class MovieController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @HystrixCommand(fallbackMethod = "queryByIdFallBack")
    @GetMapping("/user/{id}")
    public User queryById(@PathVariable Long id) {
        return restTemplate.getForObject("http://microserver-provider-user/user/" + id, User.class);
    }

    public User queryByIdFallBack(Long id) {
        return User.builder()
                .id(-1L)
                .username("默认用户")
                .build();
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

- 测试容错能力

    - 启动项目 eureka
    - 启动项目 microserver-provider-user
    - 启动本项目
    - 访问 `http://localhost:8002/user/1`

    ```
    <User>
        <id>1</id>
        <username>account1</username>
        <name>张三</name>
        <age>20</age>
        <balance>100.00</balance>
    </User>
    ```

    - 停止 microserver-provider-user

    - 再次访问 `http://localhost:8002/user/1`

    ```
    <User>
        <id>-1</id>
        <username>默认用户</username>
        <name/>
        <age/>
        <balance/>
    </User>
    ```

## 总结

通过 MovieController 代码可知, 为 queryById 提供了一个回退方法 queryByIdFallBack
当服务提供者访问出现问题的时候, 就会返回该方法提供的默认值. 

@HystrixCommand 配置非常灵活, 可使用HystrixCommand 的commandProperties属性配置, 例如

```
@HystrixCommand(fallbackMethod = "queryByIdFallBack", commandProperties = {
        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
        @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "10000")
}, threadPoolProperties = {
        @HystrixProperty(name = "coreSize", value = "1"),
        @HystrixProperty(name = "maxQueueSize", value = "10")
})
@GetMapping("/user/{id}")
public User queryById(@PathVariable Long id) {
    return restTemplate.getForObject("http://microserver-provider-user/user/" + id, User.class);
}
```

更多的配置属性请访问 https://github.com/Netflix/Hystrix/tree/master/hystrix-contrib/hystrix-javanica#configuration

## Feign 使用 Hystrix

之前的代码是使用注解 @HystrixCommand 的 fallbackMethod 属性实现回退. 然而 Feign 是以接口的形式工作的, 它没有方法体, 前文讲解的方式显然不适合 Feign.

- 复制 microserver-consume-movie-feign 修改 ArtifactId 为 microserver-consume-movie-feign-hystrix

- 修改之前的接口

```
@FeignClient(name = "microserver-provider-user", fallback = FeignClientFallback.class)
public interface UserFeignClient {

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public User queryById(@PathVariable("id") Long id);
}

@Component
class FeignClientFallback implements UserFeignClient {

    @Override
    public User queryById(Long id) {
        return User.builder()
                .id(-1L)
                .name("默认用户")
                .build();
    }
}
```









