# 服务消费者自定义 Ribbon 配置
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