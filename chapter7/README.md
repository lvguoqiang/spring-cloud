# 使用 Zuul 构建微服务网关

## 为什么要使用微服务网关

不同的微服务一般会有不同的网络地址, 而外部客户端(如手机 app)可能需要多个微服务的接口才能完成一个业务需求. 如果让客户端直接与各个微服务通信, 那么就会存在以下问题

- 客户端多次请求不同的微服务, 增加了客户端的复杂性	
- 存在跨域请求, 在某些场景下处理起来就比较复杂
- 认证复杂, 每个服务都要独立认证
- 难以重构, 随着项目的迭代, 可能需要重新划分微服务.
- 某些微服务可能使用了防火墙/浏览器不友好的协议, 直接访问会有一定的困难.

当使用了微服务网关之后, 客户端只需要和网关打交道就好, 无需直接调用特定微服务的接口. 这样使得开发得到简化, 而且微服务网关有以下优点

- 易于监控. 可在微服务网关收集监控数据并将其推送到外部系统进行分析.
- 易于认证. 可在微服务网关进行认证, 然后将请求转发到后端的微服务, 而无需在每个微服务进行监控
- 减少了客户端与各个微服务之间的交互次数

## Zuul 简介
Zuul 是 Netflix 开源的微服务网关, 它可以与 Eureka, Ribbon, Hystrix 等组件配合使用. Zuul 的核心是一系列的过滤器, 它可以实现下列功能

- 身份与认证安全: 识别每个资源的验证要求, 并决绝那些与要求不符的资源
- 审查与监控: 在边缘位置追踪有意义的数据和统计结果, 从而带来精确的生产视图.
- 动态路由: 动态的将请求路由到不同的后端集群
- 压力测试: 逐渐增加指向集群的流量, 以了解性能
- 负载分配: 为每一种负载类型分配对应容量, 并弃用查出限定值的请求.
- 静态响应处理: 在边缘位置直接建立部分响应, 避免其转发到内部集群.
- 多区域弹性: 跨越 AWS Region 进行请求路由. 旨在实现 ELB(Elastic Balancing) 使用的多样化.

## 编写 Zuul 微服务网关

- 创建项目, ArtifactId 是 microserver-gateway-zuul, 并添加依赖

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

- 启动类添加注解

```
@SpringBootApplication
@EnableZuulProxy
public class MicroserverGatewayZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserverGatewayZuulApplication.class, args);
	}
}
```

- 编写配置文件

```
server:
  port: 8040
spring:
  application:
    name: microserver-gateway-zuul
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### 测试

- 测试简单网关功能

1. 启动 spring-cloud-starter-eureka-server eure项目
2. 启动 microserver-provider-user 服务提供者
3. 启动 microserver-consume-movie-ribbon 服务消费者项目
4. 启动本项目
5. 访问 `http://peer1:8040/microserver-consume-movie/user/1` , 这个请求将会转发到 `http://peer1:8002/user/1`
6. 访问 `http://peer1:8040/microserver-provider-user/user/1`, 这个请求将会转发到 `http://peer1:8035/user/1`