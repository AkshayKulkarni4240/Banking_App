package com.practice.banking_app.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private Double amount;
    private String description; // Optional description
}