package com.practice.banking_app.dto;

import com.practice.banking_app.entity.BankAccount;
import com.practice.banking_app.entity.Transaction;
import lombok.Data;

import java.util.List;

@Data
public class AccountSummary {
    private Integer totalAccounts;
    private Double totalBalance;
    private Integer totalTransactions;
    private Double totalDeposits;
    private Double totalWithdrawals;
    private List<Transaction> recentTransactions;
    private List<BankAccount> accounts;
}