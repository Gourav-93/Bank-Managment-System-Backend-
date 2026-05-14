// TransactionImpl.java
package com.TBMS.Bank.Managment.System.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TBMS.Bank.Managment.System.Dto.TransactionDto;
import com.TBMS.Bank.Managment.System.Entity.Transaction;
import com.TBMS.Bank.Managment.System.Repository.TransactionRepository;

@Service
public class TransactionImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status(transactionDto.getStatus())
                .build();

        transactionRepository.save(transaction);

        log.info("Transaction saved for account: {}", transaction.getAccountNumber());
    }
}