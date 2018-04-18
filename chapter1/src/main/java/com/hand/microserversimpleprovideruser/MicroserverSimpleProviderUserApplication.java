package com.hand.microserversimpleprovideruser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.hand.microserversimpleprovideruser.mapper")
public class MicroserverSimpleProviderUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserverSimpleProviderUserApplication.class, args);
	}
}
