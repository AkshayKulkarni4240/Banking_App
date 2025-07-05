package com.practice.banking_app.dto;

import lombok.Data;

@Data
public class AdminTransactionRequest {
    private Double amount;
    private String description;
}