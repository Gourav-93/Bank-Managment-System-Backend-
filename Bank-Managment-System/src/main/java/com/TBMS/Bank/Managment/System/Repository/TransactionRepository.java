package com.TBMS.Bank.Managment.System.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TBMS.Bank.Managment.System.Entity.Transaction;

public interface  TransactionRepository extends JpaRepository<Transaction, String>
{
 
        List<Transaction> findByAccountNumberAndCreatedAtBetween(
        String accountNumber,
        LocalDateTime start,
        LocalDateTime end
);

}
