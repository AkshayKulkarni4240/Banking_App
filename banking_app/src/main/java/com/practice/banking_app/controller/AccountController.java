package com.practice.banking_app.controller;

import com.practice.banking_app.dto.AccountRequest;
import com.practice.banking_app.dto.TransactionRequest;
import com.practice.banking_app.entity.BankAccount;
import com.practice.banking_app.entity.User;
import com.practice.banking_app.service.BankAccountService;
import com.practice.banking_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final BankAccountService bankAccountService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountRequest request) {
        try {
            User user = userService.getCurrentUser();
            BankAccount account = bankAccountService.createAccount(user, request.getInitialAmount());
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating account");
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserAccounts() {
        try {
            User user = userService.getCurrentUser();
            List<BankAccount> accounts = bankAccountService.getAccountsByUser(user);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving accounts");
        }
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long accountId, @RequestBody TransactionRequest request) {
        try {
            User user = userService.getCurrentUser();
            BankAccount account = bankAccountService.deposit(user, accountId, request.getAmount());
            return ResponseEntity.ok("Deposit successful. New balance: " + account.getBalance());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing deposit");
        }
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long accountId, @RequestBody TransactionRequest request) {
        try {
            User user = userService.getCurrentUser();
            BankAccount account = bankAccountService.withdraw(user, accountId, request.getAmount());
            return ResponseEntity.ok("Withdrawal successful. New balance: " + account.getBalance());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing withdrawal");
        }
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getAccountBalance(@PathVariable Long accountId) {
        try {
            User user = userService.getCurrentUser();
            BankAccount account = bankAccountService.getAccountByIdAndUser(accountId, user);
            return ResponseEntity.ok(account.getBalance());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving balance");
        }
    }
}