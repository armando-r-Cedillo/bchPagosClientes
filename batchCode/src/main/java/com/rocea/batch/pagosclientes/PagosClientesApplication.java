package com.rocea.batch.pagosclientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@SpringBootApplication(exclude = {
//		DataSourceAutoConfiguration.class
//})
public class PagosClientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagosClientesApplication.class, args);
	}

}
