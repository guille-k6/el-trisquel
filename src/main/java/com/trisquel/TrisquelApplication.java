package com.trisquel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class TrisquelApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrisquelApplication.class, args);
	}

}
