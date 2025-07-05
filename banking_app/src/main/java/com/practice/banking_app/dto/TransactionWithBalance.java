package com.practice.banking_app.dto;

    import com.practice.banking_app.entity.Transaction;
    import lombok.Data;

    import java.time.LocalDateTime;

    @Data
    public class TransactionWithBalance {
        private Long id;
        private Double amount;
        private Transaction.TransactionType type;
        private LocalDateTime timestamp;
        private String description;
        private Double balanceAfterTransaction;
    }