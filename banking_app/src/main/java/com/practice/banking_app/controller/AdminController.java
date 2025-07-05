package com.practice.banking_app.controller;

import com.practice.banking_app.dto.SystemStats;
import com.practice.banking_app.entity.BankAccount;
import com.practice.banking_app.entity.Transaction;
import com.practice.banking_app.entity.User;
import com.practice.banking_app.service.BankAccountService;
import com.practice.banking_app.service.TransactionService;
import com.practice.banking_app.service.UserService;
import com.practice.banking_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UserRepository userRepository;
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final UserService userService;


    
    // Get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccount>> getAllAccounts() {
        List<BankAccount> accounts = bankAccountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

  
    @GetMapping("/users/{userId}/account")
    public ResponseEntity<BankAccount> getUserAccount(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            BankAccount account = bankAccountService.getAccountByUser(user);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

 
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

   
    @GetMapping("/users/{userId}/transactions")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Transaction> transactions = transactionService.getTransactionsByUser(user);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

  
    @PostMapping("/users/{userId}/deposit")
    public ResponseEntity<String> adminDeposit(@PathVariable Long userId, @RequestParam Double amount) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            bankAccountService.deposit(user, amount);
            return ResponseEntity.ok("Admin deposit successful for user: " + user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/withdraw")
    public ResponseEntity<String> adminWithdraw(@PathVariable Long userId, @RequestParam Double amount) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            bankAccountService.withdraw(user, amount);
            return ResponseEntity.ok("Admin withdrawal successful for user: " + user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // // Delete user (admin only)
    // @DeleteMapping("/users/{userId}")
    // public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
    //     try {
    //         if (userRepository.existsById(userId)) {
    //             userRepository.deleteById(userId);
    //             return ResponseEntity.ok("User deleted successfully");
    //         } else {
    //             return ResponseEntity.notFound().build();
    //         }
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    //     }
    // }

 @DeleteMapping("/users/{userId}")
public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
    try {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Delete associated bank accounts
        List<BankAccount> accounts = bankAccountService.getAccountsByUser(user);
        for (BankAccount account : accounts) {
            bankAccountService.deleteAccount(account.getId());
        }
        
        userRepository.deleteById(userId);
        return ResponseEntity.ok("User deleted successfully");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
}




   
    @GetMapping("/stats")
    public ResponseEntity<SystemStats> getSystemStats() {
        SystemStats stats = new SystemStats();
        stats.setTotalUsers(userRepository.count());
        stats.setTotalAccounts(bankAccountService.getTotalAccounts());
        stats.setTotalTransactions(transactionService.getTotalTransactions());
        stats.setTotalBalance(bankAccountService.getTotalSystemBalance());
        
        return ResponseEntity.ok(stats);
    }
}