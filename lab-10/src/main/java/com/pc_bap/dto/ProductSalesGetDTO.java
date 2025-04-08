package com.pc_bap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSalesGetDTO {
    private long id;
    private String name;
    private int remainingStock;
}
