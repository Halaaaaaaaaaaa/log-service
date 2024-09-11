package com.tisquare.petcare.demo;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/*@EnableScheduling*/
@SpringBootApplication
@EnableEncryptableProperties
public class LogServiceApplication {

	private static Logger logger = LoggerFactory.getLogger(LogServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LogServiceApplication.class, args);
		logger.info("===============================================================================================");
	}
	
}
