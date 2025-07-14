package com.example.demo.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private BigDecimal bookPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}