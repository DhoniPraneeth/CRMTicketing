package com.example.CRMTicketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import org.hibernate.cfg.Configuration;

@SpringBootApplication
public class CrmTicketingApplication {
	public static void main(String[] args) {
		SpringApplication.run(CrmTicketingApplication.class, args);
	}
}
