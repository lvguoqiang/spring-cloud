# 服务消费者自定义 Ribbon 配置(通过配置)

本例是一个随机的负载均衡策略

1. 复制 microserver-consumer-movie-ribbon, 修改 ArtifactId 为 microserver-consumer-movie-ribbon-customizing-properties
2. 添加配置信息
```
microserver-provider-user:
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```
这样的效果和通过java代码配置的一模一样