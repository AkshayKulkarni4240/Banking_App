package com.practice.banking_app.controller;

import com.practice.banking_app.dto.AccountSummary;
import com.practice.banking_app.entity.BankAccount;
import com.practice.banking_app.entity.Transaction;
import com.practice.banking_app.entity.User;
import com.practice.banking_app.service.BankAccountService;
import com.practice.banking_app.service.TransactionService;
import com.practice.banking_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getOwnTransactions() {
        User currentUser = userService.getCurrentUser();
        List<Transaction> transactions = transactionService.getTransactionsByUser(currentUser);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<?> getAccountTransactions(@PathVariable Long accountId) {
        try {
            User user = userService.getCurrentUser();
            List<Transaction> transactions = transactionService.getTransactionsByAccountAndUser(accountId, user);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving transactions");
        }
    }

    @GetMapping("/account-summary")
    public ResponseEntity<?> getAccountSummary() {
        try {
            User user = userService.getCurrentUser();
            List<BankAccount> accounts = bankAccountService.getAccountsByUser(user);
            
            if (accounts.isEmpty()) {
                return ResponseEntity.badRequest().body("No accounts found for user");
            }

            // Calculate totals across all accounts
            double totalBalance = accounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();

            List<Transaction> allTransactions = transactionService.getTransactionsByUser(user);
            
            AccountSummary summary = new AccountSummary();
            summary.setTotalAccounts(accounts.size());
            summary.setTotalBalance(totalBalance);
            summary.setTotalTransactions(allTransactions.size());
            
            // Calculate totals
            double totalDeposits = allTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.DEPOSIT)
                .mapToDouble(Transaction::getAmount)
                .sum();
                
            double totalWithdrawals = allTransactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.WITHDRAWAL)
                .mapToDouble(Transaction::getAmount)
                .sum();
                
            summary.setTotalDeposits(totalDeposits);
            summary.setTotalWithdrawals(totalWithdrawals);
            summary.setRecentTransactions(allTransactions.stream()
                .sorted((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()))
                .limit(10)
                .toList());
            summary.setAccounts(accounts);
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving account summary");
        }
    }
}