package com.kogitopoc.kogitopoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.kogitopoc.kogitopoc", "org.kie.kogito"})
public class KogitopocApplication {

	public static void main(String[] args) {
		SpringApplication.run(KogitopocApplication.class, args);
	}

}
