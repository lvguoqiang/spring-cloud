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
