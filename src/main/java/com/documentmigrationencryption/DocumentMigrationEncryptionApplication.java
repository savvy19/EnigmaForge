package com.documentmigrationencryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DocumentMigrationEncryptionApplication {
	private Logger logger= LoggerFactory.getLogger(getClass());
	public static void main(String[] args) {
		SpringApplication.run(DocumentMigrationEncryptionApplication.class,args);
	}
}
