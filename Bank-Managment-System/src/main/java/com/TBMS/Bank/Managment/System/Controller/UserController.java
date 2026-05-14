package com.TBMS.Bank.Managment.System.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.TBMS.Bank.Managment.System.Dto.BankResponce;
import com.TBMS.Bank.Managment.System.Dto.CreditDebitRequsest;
import com.TBMS.Bank.Managment.System.Dto.EnquiryRequest;
import com.TBMS.Bank.Managment.System.Dto.LoginRequest;
import com.TBMS.Bank.Managment.System.Dto.TransferRequest;
import com.TBMS.Bank.Managment.System.Dto.UserRequest;
import com.TBMS.Bank.Managment.System.Service.UserService;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BankResponce createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }

    @PostMapping("/balanceEnquiry")
    public BankResponce balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @PostMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }

    @PostMapping("/credit")
    public BankResponce creditAccount(@RequestBody CreditDebitRequsest request) {
        return userService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponce debitAccount(@RequestBody CreditDebitRequsest request) {
        return userService.debitAccount(request);
    }

    @PostMapping("/transfer")
    public BankResponce transfer(@RequestBody TransferRequest request) {
        return userService.transfer(request);
    }

    @PostMapping("/login")
    public BankResponce login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}