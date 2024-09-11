package com.tisquare.petcare.service;

import com.tisquare.petcare.demo.config.JasyptConfig;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootTest(classes = JasyptConfig.class)
class DemoApplicationTests {

	final BeanFactory beanFactory = new AnnotationConfigApplicationContext(JasyptConfig.class);
	final StringEncryptor stringEncryptor = beanFactory.getBean("jasyptStringEncryptor", StringEncryptor.class);

	@Test
	void contextLoads() {
	}

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
//		stringEncryptor.setConfig(config);

		String encUsername = stringEncryptor.encrypt("user_service");
		String encPassword = stringEncryptor.encrypt("user_service.123");
		String encKey = stringEncryptor.encrypt("petcare.123");
		String decUsername = stringEncryptor.decrypt(encUsername);
		String decPassword = stringEncryptor.decrypt(encPassword);
		String decKey = stringEncryptor.decrypt(encKey);

		System.out.println("Encrypted Username: " + encUsername);
		System.out.println("Encrypted Password: " + encPassword);
		System.out.println("Encrypted key: " + encKey);
		System.out.println("Decrypted Username: " + decUsername);
		System.out.println("Decrypted Password: " + decPassword);
		System.out.println("Decrypted key: " + decKey);
	}


}
