package com.tisquare.petcare.test;

import com.tisquare.petcare.log.config.JasyptConfig;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootTest(classes = JasyptConfig.class)
class testJasypt {

	final BeanFactory beanFactory = new AnnotationConfigApplicationContext(JasyptConfig.class);
	final StringEncryptor stringEncryptor = beanFactory.getBean("jasyptStringEncryptor", StringEncryptor.class);

	@Test
	void testEncrypt() {
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();

		config.setPassword("petcare.123");
		config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
		config.setKeyObtentionIterations(1000);
		config.setPoolSize(1);
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
		config.setStringOutputType("base64");

		((PooledPBEStringEncryptor) stringEncryptor).setConfig(config);

		String encDbUrl = stringEncryptor.encrypt("jdbc:postgresql://192.168.30.40:5433/petcare_user");
		String encDbUsername = stringEncryptor.encrypt("petcare");
		String encDbPassword = stringEncryptor.encrypt("petcare.123");
		String encMqHost = stringEncryptor.encrypt("192.168.30.40");
		String encMqUsername = stringEncryptor.encrypt("user_service");
		String encMqPassword = stringEncryptor.encrypt("user_service.123");
		String encKey = stringEncryptor.encrypt("petcare.123");

		String decDbbUrl = stringEncryptor.decrypt(encDbUrl);
		String decDbUsername = stringEncryptor.decrypt(encDbUsername);
		String decDbPassword = stringEncryptor.decrypt(encDbPassword);
		String decMqHost = stringEncryptor.decrypt(encMqHost);
		String decMqUsername = stringEncryptor.decrypt(encMqUsername);
		String decMqPassword = stringEncryptor.decrypt(encMqPassword);
		String decKey = stringEncryptor.decrypt(encKey);

		System.out.printf("Database URL: %s%n" +
						"Database Username: %s%n" +
						"Database Password: %s%n" +
						"RabbitMQ Host: %s%n" +
						"RabbitMQ Username: %s%n" +
						"RabbitMQ Password: %s%n" +
						"Jasypt password: %s%n%n" +
						"Decrypted DB URL: %s%n" +
						"Decrypted DB Username: %s%n" +
						"Decrypted DB Password: %s%n" +
						"Decrypted MQ Host: %s%n" +
						"Decrypted MQ Username: %s%n" +
						"Decrypted MQ Password: %s%n" +
						"Decrypted Jasypt password: %s%n",
				decDbbUrl, decDbUsername, decDbPassword, decMqHost,
				decMqUsername, decMqPassword, decKey,
				encDbUrl, encDbUsername, encDbPassword, encMqHost,
				encMqUsername, encMqPassword, encKey
		);
	}

}
