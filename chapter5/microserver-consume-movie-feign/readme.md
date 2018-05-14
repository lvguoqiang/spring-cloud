# 使用注解实现为服务消费者整合 Feign

- 复制项目microserver-consume-movie 修改ArgifactId 为microserver-consume-movie-feign
- 添加依赖

```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency> 
```

- 添加一个接口

```
@FeignClient(name = "microserver-provider-user")
public interface UserFeignClient {

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    public User queryById(@PathVariable("id") Long id);
}
```

- 修改 Controller 代码

```
@RestController
public class MovieController {

    @Autowired
    UserFeignClient userFeignClient;

    @GetMapping("/user/{id}")
    public User queryById(@PathVariable Long id) {
        return userFeignClient.queryById(id);
    }
}
```

- 修改启动类

```
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MicroserverConsumeMovieApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserverConsumeMovieApplication.class, args);
	}
}

```

- 测试
    - 启动注册服务
    - 启动多个用户服务
    - 启动本服务
    - 多次访问 `http://localhost:8002/user/3`

# 总结

- 通过测试结果表明 Feign 不仅实现了声明式的 REST 调用, 而且还实现了负载均衡策略
- FeignClient 注解说明
> @FeignClient 注解中的 microserver-provider-user 是客户端的名称, 即用户服务中的artifactId, 用户创建 Ribbon 负载均衡器. 