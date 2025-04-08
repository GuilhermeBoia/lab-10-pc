package com.pc_bap.dto;


import lombok.Data;

@Data
public class ProductGetDTO {
    private long id;
    private String name;
    private double price;
    private int quantity;
}

