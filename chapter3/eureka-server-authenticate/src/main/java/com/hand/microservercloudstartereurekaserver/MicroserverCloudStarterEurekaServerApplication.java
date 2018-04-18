package com.hand.microservercloudstartereurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@SpringBootApplication
@EnableEurekaServer
public class MicroserverCloudStarterEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserverCloudStarterEurekaServerApplication.class, args);
	}
}
