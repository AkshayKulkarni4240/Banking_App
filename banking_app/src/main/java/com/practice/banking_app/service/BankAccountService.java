package com.practice.banking_app.service;

import com.practice.banking_app.entity.BankAccount;
import com.practice.banking_app.entity.Transaction;
import com.practice.banking_app.entity.User;
import com.practice.banking_app.repository.BankAccountRepository;
import com.practice.banking_app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BankAccount createAccount(User user, Double initialAmount) {
        if (initialAmount == null || initialAmount < 0) {
            throw new IllegalArgumentException("Initial amount must be non-negative");
        }

        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(initialAmount);

        BankAccount savedAccount = bankAccountRepository.save(account);

        // Create initial deposit transaction if amount > 0
        if (initialAmount > 0) {
            Transaction transaction = new Transaction();
            transaction.setAmount(initialAmount);
            transaction.setType(Transaction.TransactionType.DEPOSIT);
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setUser(user);
            transaction.setAccount(savedAccount);
            transaction.setDescription("Initial deposit");
            transactionRepository.save(transaction);
        }

        return savedAccount;
    }

    public List<BankAccount> getAccountsByUser(User user) {
        return bankAccountRepository.findByUser(user);
    }

    public BankAccount getAccountByIdAndUser(Long accountId, User user) {
        return bankAccountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found or access denied"));
    }

    @Transactional
    public BankAccount deposit(User user, Long accountId, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        BankAccount account = getAccountByIdAndUser(accountId, user);
        account.setBalance(account.getBalance() + amount);
        BankAccount savedAccount = bankAccountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setDescription("Deposit");
        transactionRepository.save(transaction);

        return savedAccount;
    }

    @Transactional
    public BankAccount withdraw(User user, Long accountId, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        BankAccount account = getAccountByIdAndUser(accountId, user);

        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);
        BankAccount savedAccount = bankAccountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setDescription("Withdrawal");
        transactionRepository.save(transaction);

        return savedAccount;
    }
    // ------------

   
    public BankAccount getAccountById(Long accountId) {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public void deleteAccount(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        transactionRepository.deleteAll(transactions);
        bankAccountRepository.deleteById(accountId);
    }


    // Get all bank accounts (admin function)
    public List<BankAccount> getAllAccounts() {
        return bankAccountRepository.findAll();
    }

    // Get account by user (admin function)
    public BankAccount getAccountByUser(User user) {
        List<BankAccount> accounts = bankAccountRepository.findByUser(user);
        if (accounts.isEmpty()) {
            throw new IllegalArgumentException("No account found for user: " + user.getUsername());
        }
        return accounts.get(0); // Return first account if multiple exist
    }

    // Get total number of accounts (admin stats)
    public Long getTotalAccounts() {
        return bankAccountRepository.count();
    }

    // Get total system balance (admin stats)
    public Double getTotalSystemBalance() {
        List<BankAccount> allAccounts = bankAccountRepository.findAll();
        return allAccounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }

    // Admin deposit 
    @Transactional
    public BankAccount deposit(User user, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        BankAccount account = getAccountByUser(user);
        account.setBalance(account.getBalance() + amount);
        BankAccount savedAccount = bankAccountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setDescription("Admin deposit");
        transactionRepository.save(transaction);

        return savedAccount;
    }

    // Admin withdraw - 
    @Transactional
    public BankAccount withdraw(User user, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        BankAccount account = getAccountByUser(user);

        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);
        BankAccount savedAccount = bankAccountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.WITHDRAWAL);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setDescription("Admin withdrawal");
        transactionRepository.save(transaction);

        return savedAccount;
    }

    // private String generateAccountNumber() {
    //     String accountNumber;
    //     do {
    //         long timestamp = System.currentTimeMillis();
    //         Random random = new Random();
    //         int randomPart = random.nextInt(1000);
    //         String timestampPart = String.valueOf(timestamp).substring(6);
    //         accountNumber = timestampPart + String.format("%03d", randomPart);
    //     } while (bankAccountRepository.findByAccountNumber(accountNumber).isPresent());
    //     return accountNumber;
    // }
    private String generateAccountNumber() {
    String accountNumber;
    do {
        accountNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    } while (bankAccountRepository.findByAccountNumber(accountNumber).isPresent());
    return accountNumber;
}
}