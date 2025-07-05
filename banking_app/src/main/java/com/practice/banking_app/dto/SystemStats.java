package com.practice.banking_app.dto;

import lombok.Data;

@Data
public class SystemStats {
    private Long totalUsers;
    private Long totalAccounts;
    private Long totalTransactions;
    private Double totalBalance;
}