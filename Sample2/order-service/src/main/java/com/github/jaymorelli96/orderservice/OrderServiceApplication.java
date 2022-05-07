package com.github.jaymorelli96.orderservice;

import javax.annotation.PreDestroy;

import com.rabbitmq.client.Connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Mono;

@SpringBootApplication
public class OrderServiceApplication {

	// Connection to RabbitMQ
	@Autowired
	private Mono<Connection> connectionMono;

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	
	// Make sure the connection before the program is finished
	@PreDestroy
    public void close() throws Exception {
        connectionMono.block().close();
    }

}
