package com.hand.microserverprovideruser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan("com.hand.microserverprovideruser.mapper")
@EnableDiscoveryClient
public class MicroserverProviderUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserverProviderUserApplication.class, args);
	}
}
