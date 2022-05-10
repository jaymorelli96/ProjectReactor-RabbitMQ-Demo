package com.github.jaymorelli96.databasemanager.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    private String id;
    private Item[] items;
    private double totalCost;
}
