# 脱离 Eureka 使用 Ribbon
适用情况: 一些遗留的微服务, 可能没有注册到 Eureka; 或者根本不是用 spring cloud 开发.
脱离 Eureaka 后, 就需要将服务提供者的实例的 ip 和端口在 ribbon 信息中写全. 如果多了一个实例, 那么就需要将配置信息修改一下.

1. 复制一份 microserver-consumer-movie-ribbon, 修改 ArtifactId 为 microserver-consumer-movie-without-eureka
2. 修改依赖, 将 eureka 的依赖修改为 Ribbon 的依赖
    ```
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>
    ```
3. 添加配置信息
    ```
    microserver-provider-user:
      ribbon:
        listOfServers: localhost:8000,localhost:8001
    ```

4. 启动服务提供者-user

    ```
    java -jar microserver-provider-user-0.0.1-SNAPSHOT.jar --server.port=8000
    java -jar microserver-provider-user-0.0.1-SNAPSHOT.jar --server.port=8001
    ```
    
5. 查看日志(访问: localhost:8002/log-instance)

    ```
    2018-04-19 18:47:15.077  INFO 11360 --- [nio-8002-exec-8] c.h.m.web.MovieController                : microserver-provider-user:localhost:8000
    2018-04-19 18:47:15.246  INFO 11360 --- [nio-8002-exec-9] c.h.m.web.MovieController                : microserver-provider-user:localhost:8001
    2018-04-19 18:47:15.414  INFO 11360 --- [nio-8002-exec-2] c.h.m.web.MovieController                : microserver-provider-user:localhost:8000
    2018-04-19 18:47:15.587  INFO 11360 --- [nio-8002-exec-4] c.h.m.web.MovieController                : microserver-provider-user:localhost:8001
    2018-04-19 18:47:15.747  INFO 11360 --- [nio-8002-exec-6] c.h.m.web.MovieController                : microserver-provider-user:localhost:8000
    2018-04-19 18:47:15.916  INFO 11360 --- [nio-8002-exec-8] c.h.m.web.MovieController                : microserver-provider-user:localhost:8001
    2018-04-19 18:47:16.067  INFO 11360 --- [nio-8002-exec-9] c.h.m.web.MovieController                : microserver-provider-user:localhost:8000
    2018-04-19 18:47:16.218  INFO 11360 --- [nio-8002-exec-2] c.h.m.web.MovieController                : microserver-provider-user:localhost:8001
    ```
