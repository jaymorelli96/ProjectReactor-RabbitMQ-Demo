package com.github.jaymorelli96.orderservice.service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.github.jaymorelli96.orderservice.dto.OrderDTO;
import com.github.jaymorelli96.orderservice.model.Order;
import com.rabbitmq.client.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

@Service
public class OrderService {



    final Sender sender;

    OrderService(Sender sender) {
        this.sender = sender;
    }

    
	// Name of our Queue
	private static final String QUEUE = "demo-queue";
	// slf4j logger
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);


    
    /**
    * Send a new Order to the message queue.
    * @param dto Order DTO
    * @return Mono Order
    */
    public Mono<Order> createOrder(Mono<OrderDTO> dto) {

        return dto.flatMap(orderDto -> {

            //Validations ...

            //Map OrderDTO to Order object
            Order order = mapperOrderDTOToEntity(orderDto);

            //Serialize object to bytes, which represents the object in a portable fashion
            byte[] orderSerialized = SerializationUtils.serialize(order);

            //Outbound Message that will be sent by the Sender
            Mono<OutboundMessage> outbound = Mono.just( new OutboundMessage(
                "",
                QUEUE, orderSerialized));

            // Declare the queue then send the flux of messages.
			sender
                .declareQueue(QueueSpecification.queue(QUEUE))
                .then(sender.send(outbound))
                .doOnError(e -> LOGGER.error("Send failed", e))
                .subscribe(m -> {
                    LOGGER.info("Message sent");
				});

            //Return posted object to the client.
            return Mono.just(order);
        });
    }


    /**
     * Mapper for OrderDTO to Entity.
     * It will call calculate total cost of the order object.
     * @param dto Order DTO
     * @return Order from OrderDTO
     */
    public Order mapperOrderDTOToEntity(OrderDTO dto) {
        Order order = new Order();
        order.setItems(dto.getItems());
        order.setTotalCost(order.calculateTotalCost());

        return order;
    }
    
}
