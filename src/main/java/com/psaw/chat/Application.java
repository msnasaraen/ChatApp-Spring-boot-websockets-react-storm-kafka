package com.psaw.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application {
	public static void main(String[] args) throws Exception {
		//CreateStormTopology.createTopology();
        SpringApplication.run(Application.class, args);
    }
}
