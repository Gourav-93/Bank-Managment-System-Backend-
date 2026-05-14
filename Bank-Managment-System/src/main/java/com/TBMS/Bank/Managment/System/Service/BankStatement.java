package com.TBMS.Bank.Managment.System.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.TBMS.Bank.Managment.System.Entity.Transaction;
import com.TBMS.Bank.Managment.System.Repository.TransactionRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BankStatement {

    private TransactionRepository transactionRepository;

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) {

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        return transactionRepository
                .findByAccountNumberAndCreatedAtBetween(accountNumber, startDateTime, endDateTime);
    }

}