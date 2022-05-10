package com.github.jaymorelli96.orderservice.handler;

import com.github.jaymorelli96.orderservice.dto.OrderDTO;
import com.github.jaymorelli96.orderservice.model.Order;
import com.github.jaymorelli96.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class OrderHandler {

    
    private final OrderService service;

    public OrderHandler(OrderService service) {
        this.service = service;
    }

    
    /**
     * Recieve POST request from the router to save an Order object into the MongoDB
     * @param request ServerRequest
     * @return ServerResponse with the Order object in the body 
     */
    public Mono<ServerResponse> createOrder(ServerRequest request) {
        //1. Extract JSON object from Server Request.
        Mono<OrderDTO> dto = request.bodyToMono(OrderDTO.class);
        
        //2. Send request to service.
        Mono<Order> result = service.createOrder(dto);   

        //3. Return server response and the order object in the body.
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)    
                .body(result, Order.class);
    }
    
}
