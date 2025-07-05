package com.practice.banking_app.repository;

import com.practice.banking_app.entity.BankAccount;
import com.practice.banking_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByUser(User user);

    Optional<BankAccount> findByIdAndUser(Long id, User user);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

}