package com.github.jaymorelli96.databasemanager.model;



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

}
