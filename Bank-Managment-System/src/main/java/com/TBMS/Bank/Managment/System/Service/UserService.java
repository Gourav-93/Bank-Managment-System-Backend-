package com.TBMS.Bank.Managment.System.Service;

import com.TBMS.Bank.Managment.System.Dto.BankResponce;
import com.TBMS.Bank.Managment.System.Dto.CreditDebitRequsest;
import com.TBMS.Bank.Managment.System.Dto.EnquiryRequest;
import com.TBMS.Bank.Managment.System.Dto.LoginRequest;
import com.TBMS.Bank.Managment.System.Dto.TransferRequest;
import com.TBMS.Bank.Managment.System.Dto.UserRequest;

public interface UserService {

    BankResponce createAccount(UserRequest userRequest);

    BankResponce balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponce creditAccount(CreditDebitRequsest request);

    BankResponce debitAccount(CreditDebitRequsest request);

    BankResponce transfer(TransferRequest request);

    BankResponce login(LoginRequest request);
}