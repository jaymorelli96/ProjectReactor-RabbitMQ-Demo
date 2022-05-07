package com.github.jaymorelli96.orderservice.config;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

@Configuration
public class RabbitConfig {
	
	@Bean
    Mono<Connection> connectionMono() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.useNio();
        return Mono.fromCallable(() -> connectionFactory.newConnection("reactor-rabbit")).cache();
    }

	@Bean
	public SenderOptions senderOptions(Mono<Connection> connectionMono) {
		return new SenderOptions()
			.connectionMono(connectionMono)
            .resourceManagementScheduler(Schedulers.boundedElastic());
	} 

	@Bean
	public Sender sender(SenderOptions senderOptions) {
		return RabbitFlux.createSender(senderOptions);
	}

}
