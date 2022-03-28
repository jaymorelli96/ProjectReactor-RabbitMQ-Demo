package com.github.jaymorelli96.sender;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import com.rabbitmq.client.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.*;

@SpringBootApplication
public class SenderApplication {
	

	// Our connection to the RabbitMQ
	@Autowired
	private Mono<Connection> connectionMono;

	// Name of our Queue
	private static final String QUEUE = "demo-queue";
	// slf4j logger
    private static final Logger LOGGER = LoggerFactory.getLogger(SenderApplication.class);

	public static void main(String[] args) {
        SpringApplication.run(SenderApplication.class, args).close();
    }

	// Make sure we close the connection after the messages are sent.
	@PreDestroy
    public void close() throws Exception {
        connectionMono.block().close();
    }

	// Runner class
	@Component
    static class Runner implements CommandLineRunner {

        final Sender sender;

        Runner(Sender sender) {
            this.sender = sender;
        }

        @Override
        public void run(String... args) throws Exception {

			// Number of message that will be sent to the queue
			int messageCount = 10;
			
			// CountDownLatch to keep track that all our messages are being sent
            CountDownLatch latch = new CountDownLatch(messageCount);
			
			// Represent the list of messages that will be sent
			Flux<OutboundMessage> outboundFlux  =
				Flux.range(1, messageCount)
					.map(i -> new OutboundMessage(
						"",
						QUEUE, ("Message - " + i).getBytes()
					));
				
	
			/*
				1. Declare the queue.
				2. After the queue declaration is completed, it will call another Publisher (sendWithPublishConfirms method), which takes the flux of messages declared earlier.
				3. In case of error, it will log the error.
				4. Finally it subscribe a consumer to this flux, here we can check if the message if being acknowledged by the queue. We also decrement the CountDownLatch by one.
			*/
			sender.declareQueue(QueueSpecification.queue(QUEUE))
				.thenMany(sender.sendWithPublishConfirms(outboundFlux))
				.doOnError(e -> LOGGER.error("Send failed", e))
				.subscribe(m -> {
					if(m.isAck()) {
						LOGGER.info("Message [" + latch.getCount() + "] sent");
						latch.countDown();
					}
				});

			// Make sure all the threads are completed.
			latch.await(3L, TimeUnit.SECONDS);
        }

    }

}
