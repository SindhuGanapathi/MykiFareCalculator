package com.busfare.calculate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FareCalculatorApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(FareCalculatorApplication.class, args);

		FareProcessor service = applicationContext.getBean(FareProcessor.class);
		service.readTaps();

	}
}

