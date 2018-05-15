package com.hand.microservicediscoryeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MicroserviceDiscoryEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceDiscoryEurekaApplication.class, args);
	}
}
