# 服务消费者集成 ribbon

1. 引入依赖, eureka 的依赖中包含了 ribbon 的依赖, 所以不用再另外引入
2. 为 RestTemplate 添加 @LoadBalanced 注解
和之前唯一的区别就是多加了一个依赖

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

3. 修改 controller 

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
这样多次访问的时候就可以看到 ribbon 使得多个实例不同的访问
