package com.practice.banking_app.service;

import com.practice.banking_app.entity.Transaction;
import com.practice.banking_app.entity.User;
import com.practice.banking_app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountService bankAccountService;

    public List<Transaction> getTransactionsByUser(User user) {
        return transactionRepository.findByUserOrderByTimestampDesc(user);
    }

    public List<Transaction> getTransactionsByAccountAndUser(Long accountId, User user) {
        // Verify user owns the account
        bankAccountService.getAccountByIdAndUser(accountId, user);
        return transactionRepository.findByAccountIdOrderByTimestampDesc(accountId);
    }

   

    // Get all transactions (admin function)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTimestampDesc();
    }

    // Get total number of transactions (admin stats)
    public Long getTotalTransactions() {
        return transactionRepository.count();
    }
   
}