package com.practice.banking_app.dto;

import lombok.Data;

@Data
public class AccountRequest {
    private Double initialAmount = 0.0; 
    private String accountType; 
}