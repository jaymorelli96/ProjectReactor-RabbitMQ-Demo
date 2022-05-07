package com.github.jaymorelli96.orderservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String id;
    private Item[] items;
    private double totalCost;

    public Order(Item[] items) {
        this.items = items;
        this.totalCost = this.calculateTotalCost();
    }

    public double calculateTotalCost() {
        //1. Prepare result
        double result = 0;

        //2. Sum all items
        List<Item> itemsList = Arrays.asList(items);
        for (Item item : itemsList) {
            result += item.getPrice();
        }
        
        //3. Return value
        return result;
    }
}