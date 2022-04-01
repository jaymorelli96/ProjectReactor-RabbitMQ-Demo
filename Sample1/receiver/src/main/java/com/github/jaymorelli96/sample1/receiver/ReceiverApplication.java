package com.github.jaymorelli96.sample1.receiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import com.rabbitmq.client.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.Receiver;


@SpringBootApplication
public class ReceiverApplication {

    // Connection to RabbitMQ
    @Autowired
    private Mono<Connection> connectionMono;

	// slf4j logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiverApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReceiverApplication.class, args).close();
	}

    // Make sure the connection before the program is finished
	@PreDestroy
    public void close() throws Exception {
        connectionMono.block().close();
    }

	@Component
    static class Runner implements CommandLineRunner {
        
        //Receiver dependency 
        final Receiver receiver;

		Runner(Receiver receiver) {
            this.receiver = receiver;
        }

        @Override
        public void run(String... args) throws Exception {
            // CountDownLatch to keep track of the threads
            int messageCount = 10;
            CountDownLatch latch = new CountDownLatch(messageCount);

			//Consume messages from the queue
            Disposable disposable = receiver.consumeNoAck("demo-queue").subscribe(m -> {
                LOGGER.info("Received message {}", new String(m.getBody()));
                latch.countDown();
            });

            //Wait for threads to complete
            latch.await(3L, TimeUnit.SECONDS);

            //Close receiver and tasks
            disposable.dispose();
            receiver.close();
        }
	}

}
