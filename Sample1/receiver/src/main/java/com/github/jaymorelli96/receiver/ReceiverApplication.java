package com.github.jaymorelli96.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@SpringBootApplication
public class ReceiverApplication {
        
	@Autowired
    Mono<Connection> connectionMono;

	// slf4j logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReceiverApplication.class, args);
	}

    @PreDestroy
    public void close() throws Exception {
        connectionMono.block().close();
    }

	
	// Runner class
	@Component
    static class Runner implements CommandLineRunner {

        final Flux<Delivery> deliveryFlux;

		Runner(Flux<Delivery> deliveryFlux) {
            this.deliveryFlux = deliveryFlux;
        }

        @Override
        public void run(String... args) throws Exception {

			//Consume messages from the queue
            deliveryFlux.subscribe(m -> {
                LOGGER.info("Received message {}", new String(m.getBody()));
            });

        }
	}

}
