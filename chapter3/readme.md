# 微服务的注册与发现

## 服务发现简介
1. 服务提供者, 服务消费者, 服务发现组件之间的关系, 
	* 各个微服务在启动的时候会把自己的网络地址等信息注册到服务发现组件中, 服务发现组件会存储这些信息
	* 服务消费者可以从发现组件查询服务提供者的网络地址, 并使用该地址调用提供者的接口
	* 各个微服务与服务发现组件使用一定的机制(例如心跳)通信, 服务发现组件如果长时间无法与某微服务通信, 就会注销该实例
	* 微服务网络地址发现变更(例如, 实例增减或者ip端口反生改变)时, 会重新注册到服务发现组件. 使用这种机制, 服务消费者就无需人工改变提供者的网络地址了.
2. 服务发现组件应具备以下功能
	* 服务注册表: 是服务发现组件的核心, 用来记录各个微服务的信息, 例如 微服务的名称, ip, 端口等. 服务注册表提供查询 API 和管理 API, 查询 API 用于查询可用的微服务实例, 管理 API 用于服务的注册和注销
	* 服务注册与服务发现: 服务注册是指服务启动时, 将自己的信息发送到服务发现组价的过程. 服务发现是指, 查询可用微服务列表及网络地址的机制.
	* 服务检查: 服务发现组件使用一定的机制定时检测已注册的服务, 如发现某实例长时间无法访问, 就会从服务注册表中移除该实例.

## Eureak 简介

1. Eureka 有 Eureka Server 和 Eureka client 两个组件, 他们的作用
	* Eureka Server 提供服务发现的能力, 各个微服务启动时, 会向 Eureka Server 注册自己的信息, Eureka Service 会存储这些信息
	* Eureka Client 是一个 Java 客户端, 用于简化与 Eureka 的交互.
	* 微服务启动后, 会周期性(默认30秒)地向 Eureka Server 发送心跳以续约自己的 "租期"
	* 如果 Eureka Server 长时间没有收到某个微服的心跳, Eureka Server 会注销该实例(默认是90秒)
	* 默认情况下, Eureka Server 也是一个 Eureka client. 多个 Eureka Server 实例, 相互之间通过复制的方式来实现服务注册表的数据同步
	* Eureka clien 会缓存服务注册表的信息. 这种方式有一定的优势
		* 微服务无须每次请求都查询 Eureka Server, 从而降低了 Eureka Server 的压力
		* 即使所有的 Eureka Server 节点都宕掉, 服务消费者依然可以从缓存中找到服务提供者并完成调用	
2. 编写 Eureka
	* 依赖

	```
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
	</dependency>
	```
	* 配置信息

	```
	eureka:
	  client:
	    register-with-eureka: false   #是否将自己注册到 Eureka Server 默认为true
	    fetch-registry: false       #是否从 Eureka Server 获取注册信息, 默认为 true
	    service-url:
	      defaultZone: http://localhost:8761/eureka/  #设置与 Eureka Server 交互的地址, 多个地址可用 , 隔开
	```
	* Application 加注解 `@EnableEurekaServer`

## 将之前的用户微服务注册到 eureka server

1. 复制之前的项目 microserver-simple-provider-user, 将 AritifactId 修改为 microserver-provider-user
2. 添加依赖

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-dependencies</artifactId>
			<version>Finchley.M9</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

3. 配置 application.yml

```
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

4. 启动类加注解 `@EnableDiscoveryClient`

## 将之前的电影微服务注册到 Eureka Server 上

1. 复制之前的 Microserver-simple-consumber-movie, 并将 AritifactId 修改为 microserver-consumber-movie
2. 添加依赖

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-dependencies</artifactId>
			<version>Finchley.M9</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

3. 配置 application.yml

```
spring:
  application:
    name: microserver-consume-movie
eureka:
  instance:
    prefer-ip-address: true
  client:
    service-url:
      defaultZone: http://localhost:8761/Eureka
```

## Eureka Server 的高可用

1. 简述
单节点的 Eureka Server 并不合适线上生产环境. 因为我们要做到即使 Eureka Server 发生宕机也不会影响微服务之间的调用. 我们这里将配置一个双节点的Eureka Server 集群

2. 复制项目 microserver-discovery-eureka, 将 ArtifactId 修改为 microserver-discover-eureka-ha.

3. 配置系统中的 hosts, Windows 系统的路径为: C:\Windows\System32\driver\etc\hosts; Linux 及 Mac OS 等系统的文件路径是 /etc/hosts. 
127.0.0.1 peer1 peer2

4. 配置 application.yml

```
spring:
  application:
    name: microserver-discory-eureka-ha
---

spring:
  #指定profiles 为peer1
  profiles: peer1
server:
  port: 8761
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8762/eureka/ #设置与 Eureka Server 交互的地址, 多个地址可用 , 隔开
  instance:
    hostname: peer1
---
spring:
  profiles: peer2
server:
  port: 8762
eureka:
  instance:
    hostname: peer2
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/ #设置与 Eureka Server 交互的地址, 多个地址可用 , 隔开
```

5. 启动测试

```
java -jar microserver-cloud-starter-eureka-server-ha-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1
java -jar microserver-cloud-starter-eureka-server-ha-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer2
```
可以发现在 `localhost:8761` 节点可以发现 peer2, 在 `localhost:8762` 可以发现 peer1

## 将项目注册到集群
以 microserver-consume-movie为例说明
1. 配置 application.yml

```
eureka:
  instance:
    prefer-ip-address: true
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/,http://localhost:8762/eureka/
```
这样配置的效果与单个节点的配置是一样的, 但是最好还是配置多个节点

## 为 Eureka Server 添加用户认证

1. 复制项目 microserver-discovery-eureka, 将 ArtifactId 修改为 microserver-discover-eureka-authenticate.

2. 添加依赖

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

3. 配置 application.yml
之前的 serurity.basic.enable 等已经过时, 需要配置成下面的

```
spring:
  security:
    user:
      name: user
      password: password123
```

4. 配置 WebSecurityConfig
由于我的 spring cloud 版本是 Finchley.M9, 用户名和密码的配置变成了 3, 然后在客户端连接的时候就各种报错
`Cannot execute request on any known server`, 导致服务器注册失败, 折腾了好久, 原来是SpringBoot从2.0.0.RC1升级到2.0.0.RELEASE的时候，有个类SpringBootWebSecurityConfiguration发生了变化. 所以需要配置 WebSecurityConfig

```
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);      
        http.csrf().disable();
    }
}
```

5. 微服务注册到 Eureka Server
只需要在地址前面添加 `user:password123@` 即可

```
eureka:
  instance:
    prefer-ip-address: true
  client:
    service-url:
      defaultZone: http://user:password123@localhost:8761/eureka/,http://user:password123@localhost:8762/eureka/
```