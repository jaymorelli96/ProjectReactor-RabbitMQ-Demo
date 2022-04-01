package com.github.jaymorelli96.sample1.receiver.config;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;

@Configuration
public class RabbitConfig {

    @Bean()
    Mono<Connection> connectionMono() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.useNio();
        return Mono.fromCallable(() -> connectionFactory.newConnection("receiver-connection")).cache();
    }

	@Bean
	public ReceiverOptions receiverOptions(Mono<Connection> connectionMono) {
		return new ReceiverOptions()
			.connectionMono(connectionMono);
	} 

    @Bean
    Receiver receiver(ReceiverOptions receiverOptions) {
        return RabbitFlux.createReceiver(receiverOptions);
    }

}
