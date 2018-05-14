package com.hand.microservergatewayzuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class MicroserverGatewayZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserverGatewayZuulApplication.class, args);
	}
}
