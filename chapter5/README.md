# Feign 实现声明式 REST 调用
> 之前的实现调用的方式是采用 RestTemplate 实现的, 里面的参数都是通过参数拼接的, 但是如果参数比较多的情况下, 比如超过10个, 那么就会变得很低效并且难以维护. 那么这个问题如何解决呢? 

## 为消费者整合 Feign

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

## 总结

- 通过测试结果表明 Feign 不仅实现了声明式的 REST 调用, 而且还实现了负载均衡策略
- FeignClient 注解说明
> @FeignClient 注解中的 microserver-provider-user 是客户端的名称, 即用户服务中的artifactId, 用户创建 Ribbon 负载均衡器. 

# Feign 构造多参数请求

## Get 请求多参数的URL

假设有一个请求是 http://localhost:8002/user/get?id=3&name=张三 那么使用 Feign 如何构造呢

- 方法一

通过在 UserFeignClient 接口中写多个参数来指明, 需要几个参数, 在方法中就指定几个参数

```
@FeignClient(name = "microserver-provider-user")
public interface UserFeignClient {

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public User queryById(@RequestParam("id") Long id, @RequestParam("name") String name);
```

```
@RestController
public class MovieController {

    @Autowired
    UserFeignClient userFeignClient;

    @GetMapping("/user")
    public User queryById(@RequestParam Long id, @RequestParam String name) {
        return userFeignClient.queryById(id,name);
    }
}
```

- 方法二

使用 map 集合来构建. 当参数非常多时, 可以使用 map 来简化 feign 接口的编写.

```
@FeignClient(name = "microserver-provider-user")
public interface UserFeignClient {

    /**
     *
     * @param map
     * @return
     */
    @GetMapping("/get")
    public User queryById(@RequestParam("id") Map<String, Object> map);
}
```

调用时这样构造.

```
@RestController
public class MovieController {

    @Autowired
    UserFeignClient userFeignClient;

    @GetMapping("/user")
    public User queryById(@RequestParam Long id, @RequestParam String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 3);
        map.put("name","张三");
        return userFeignClient.queryById(map);
    }
}

```

## POST 请求的多参数构造

Post 多参数的构造就非常简单了.

```
@FeignClient(name = "microserver-provider-user")
public interface UserFeignClient {

    /**
     *
     * @param user
     * @return
     */
    @PostMapping("/post")
    public User insert(@RequestBody User user);
}
```