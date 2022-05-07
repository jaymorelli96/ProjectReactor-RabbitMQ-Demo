package com.github.jaymorelli96.databasemanager.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.github.jaymorelli96.databasemanager.model.Order;
import com.rabbitmq.client.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

@Service
public class OrderService {


    final Receiver receiver;

    OrderService(Receiver receiver) {
        this.receiver = receiver;
    }

    
	// Name of our Queue
	private static final String QUEUE = "demo-queue";
	// slf4j logger
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    
    @PostConstruct
    private void init()  {
        consume();
    }
  

    public Disposable consume() {
        //Consume messages from the queue
       return receiver.consumeAutoAck(QUEUE).subscribe(m -> {
            Order order = (Order) SerializationUtils.deserialize(m.getBody());
            LOGGER.info("Received message {}", order.getTotalCost());
        });
    }

}
