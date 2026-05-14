package com.TBMS.Bank.Managment.System.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.TBMS.Bank.Managment.System.Entity.Transaction;
import com.TBMS.Bank.Managment.System.Service.BankStatement;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/bankstatement")
public class TransactionController {

    @Autowired
    private BankStatement bankStatement;

    @GetMapping
    public List<Transaction> generateStatement(
            @RequestParam String accountNumber,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }
}
