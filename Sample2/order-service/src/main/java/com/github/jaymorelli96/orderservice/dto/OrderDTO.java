package com.github.jaymorelli96.orderservice.dto;

import com.github.jaymorelli96.orderservice.model.Item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    
    private String id;
    private Item[] items;
    
}
