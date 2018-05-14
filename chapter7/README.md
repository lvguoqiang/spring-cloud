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

- 测试负载均衡

1. 启动 spring-cloud-starter-eureka-server eure项目
2. 启动多个 microserver-provider-user 服务提供者
3. 启动本项目
4. 多次访问 `http://peer1:8040/microserver-provider-user/user/1`, 发现每个用户微服务下都会打印以下日志

```
 JDBC Connection [HikariProxyConnection@1768137372 wrapping com.mysql.jdbc.JDBC4Connection@3bebc9f7] will not be managed by Spring
2018-05-14 17:52:09.116 DEBUG 13972 --- [nio-8004-exec-9] org.mybatis.spring.SqlSessionUtils       : Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@22ad5cf]
```
这说明 zuul 可以达到负载均衡的效果

## 路由配置详解

- 自定义指定微服务的访问路径

```
zuul:
  routes:
    microserver-provider-user:	/user/**
```
这样设置之后, microserver-provider-user 的服务就会映射到 /user/** 路径

- 忽略指定微服务

```
zuul:
  ignored-services: microserver-provider-user, microserver-consume-movie
```
这样就可以忽略 microserver-provider-user 和 microserver-consume-movie微服务, 只代理其它的微服务

- 忽略所有微服务, 只代理指定的微服务

```
zuul:
  ignored-services: '*'
  routes:
  	microserver-provider-user:	/user/**
```
这样就能只路由 microserver-provider-user 微服务

- 同时指定微服务的 serviceId 和对应路径

```
zuul:
  routes:
  	user-route:		#user-route只是给路由起一个名字, 可以任意起
  	  service-id:  microserver-provider-user
  	  path:  /user/**
```

- 同时指定 path 和 URL

```
zuul:
  routes:
    user-route:
      url: http://localhost:8000/
      path: /user/**
```
这样就可以将 /user/** 映射到 http://localhost:8000/**, 需要注意的是这种方式配置的路由不会作为 HystrixCommand 执行, 同时也不能使用 Ribbon 负载多个 URL. 
- 同时指定 path 和 URL, 并且不会破坏 Zuul 的 Hystrix, Ribbon 特性

```
zuul:
  routes:
  	user-route:
  	  service-id:  microserver-provider-user
  	  path:  /user/**
ribbon:
  eureka: 
    enabled: false
microserver-provider-user:
  robbon: 
    listOfServers: localhost:8000, localhost:8001
```

- 使用正则表达式指定 Zuul 的路由匹配规则

```
@Bean
public PatternServiceRouteMapper serviceRouteMapper() {
	return new PatternServiceRouteMapper("(?<name>^.+)-(?<version>v.+$)","${version}/${name}");
}
```
这样可以将类似于 microserver-provider-user-1 的微服务, 映射到 /v1/microserver-provider-user-1

- 路由前缀

```
zuul:
  prefix: /api
  strip-prefix: false
  routes:
    microserver-provider-user:	/user/**
```
这样访问 zuul 的 /microserver-provider-user/1 将被转发为 /api/microserver-provider-user/1

# Zuul 的过滤器

## 过滤器类型与请求生命周期

- PRE: 这种过滤器在请求被路由之前调用. 可利用这种过滤器实现身份认证, 在集群中选择请求的微服务, 记录调试信息等. 
- ROUTING: 这种过滤器将请求路由到微服务. 这种过滤器用于构建发送给微服务的请求, 并使用 Appache HttpClient 或 Netfilx Ribbon 请求微服务.
- POST: 这种过滤器在路由微服务以后执行. 这种过滤器可用于为响应添加标准的 HTTP Header, 收集统计信息和指标, 将响应从微服务发送给客户端等.
- ERROR: 在其它阶段发生错误时执行该过滤器.

除了默认的过滤器类型, Zuul 还允许创建自定义的过滤器类型. 例如可以定制一种 STATIC 类型的过滤器, 直接在 Zuul 中生成响应, 而不将请求转发到后端的微服务.

## 编写 Zuul 过滤器
现在编写一个 Zuul 过滤器, 将该过滤器打印请求日志

- 复制项目 microserver-zuul 将, ArtifactId 修改为 microserver-gateway-zuul-filter
- 编写自定义 Zuul 过滤器

```
public class PreRequestLogFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(PreRequestLogFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        logger.info(String.format("send %s request to %s", request.getMethod(), request.getRequestURL().toString()));
        return null;
    }
}
```
方法解析:
	- filterType: 返回过滤器的类型. 有 pre, route, post, error等几种取值. 分别对应上文的几种过滤类型
	- filterOrder: 返回一个 int 值来指定过滤器的执行顺序, 不同的过滤器允许返回相同的值
	- shouldFiler: 返回一个 boolean 值来判断该过滤器是否要执行, true 表示执行, false 表示不执行.
	- run: 过滤的具体逻辑.

- 修改启动类, 添加以下内容

```
@Bean
	public PreRequestLogFilter preRequestLogFilter() {
		return new PreRequestLogFilter();
	}
```

