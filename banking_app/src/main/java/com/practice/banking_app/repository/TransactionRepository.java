package com.practice.banking_app.repository;

import com.practice.banking_app.entity.BankAccount;
import com.practice.banking_app.entity.Transaction;
import com.practice.banking_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByTimestampDesc(User user);
    List<Transaction> findByAccountOrderByTimestampDesc(BankAccount account);
     List<Transaction> findByAccountIdOrderByTimestampDesc(Long accountId);
    List<Transaction> findAllByOrderByTimestampDesc();
    List<Transaction> findByAccountId(Long accountId);

}