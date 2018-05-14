# 通用方式整合 Hystrix

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













