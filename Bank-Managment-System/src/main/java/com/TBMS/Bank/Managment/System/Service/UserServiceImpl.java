package com.TBMS.Bank.Managment.System.Service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.TBMS.Bank.Managment.System.Utils.AccountUtils;
import com.TBMS.Bank.Managment.System.Dto.AccountInfo;
import com.TBMS.Bank.Managment.System.Dto.BankResponce;
import com.TBMS.Bank.Managment.System.Dto.CreditDebitRequsest;
import com.TBMS.Bank.Managment.System.Dto.EmailDetails;
import com.TBMS.Bank.Managment.System.Dto.EnquiryRequest;
import com.TBMS.Bank.Managment.System.Dto.LoginRequest;
import com.TBMS.Bank.Managment.System.Dto.TransactionDto;
import com.TBMS.Bank.Managment.System.Dto.TransferRequest;
import com.TBMS.Bank.Managment.System.Dto.UserRequest;
import com.TBMS.Bank.Managment.System.Entity.UserEntity;
import com.TBMS.Bank.Managment.System.Repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

        private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

        @Autowired
        UserRepository userRepository;

        @Autowired
        TransactionService transactionService;

        @Autowired
        EmailService emailService;

        @Override
        public BankResponce createAccount(UserRequest userRequest) {

                if (userRepository.existsByEmail(userRequest.getEmail())) {
                        return BankResponce.builder()
                                        .responcecode(AccountUtils.ACCOUNT_EXISTS_CODE)
                                        .responcemessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                UserEntity newUser = UserEntity.builder()
                                .firstName(userRequest.getFirstName())
                                .lastName(userRequest.getLastName())
                                .othername(userRequest.getOthername())
                                .gender(userRequest.getGender())
                                .address(userRequest.getAddress())
                                .stateOfOrigin(userRequest.getStateOfOrigin())
                                .accountNumber(AccountUtils.generateAccountNumber())
                                .accountBalance(BigDecimal.ZERO)
                                .email(userRequest.getEmail())
                                .phoneNumber(userRequest.getPhoneNumber())
                                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                                .status("Active")
                                .build();

                UserEntity savedUser = userRepository.save(newUser);

                EmailDetails emailDetails = EmailDetails.builder()
                                .recipient(savedUser.getEmail())
                                .subject("Account Creation Successful")
                                .messageBody("Your account has been created successfully!\n" +
                                                "Account Name: " + savedUser.getFirstName() + " "
                                                + savedUser.getLastName() +
                                                "\nAccount Number: " + savedUser.getAccountNumber() +
                                                "\nBalance: " + savedUser.getAccountBalance())
                                .build();

                emailService.sendEmailAlert(emailDetails);

                return BankResponce.builder()
                                .responcecode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                                .responcemessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                                .accountInfo(AccountInfo.builder()
                                                .accountBalance(savedUser.getAccountBalance())
                                                .accountNumber(savedUser.getAccountNumber())
                                                .accountName(savedUser.getFirstName() + " " + savedUser.getLastName())
                                                .build())
                                .build();
        }

        @Override
        public BankResponce balanceEnquiry(EnquiryRequest request) {

                if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
                        return BankResponce.builder()
                                        .responcecode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                                        .responcemessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                UserEntity user = userRepository.findByAccountNumber(request.getAccountNumber());

                return BankResponce.builder()
                                .responcecode(AccountUtils.ACCOUNT_FOUND_CODE)
                                .responcemessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                                .accountInfo(AccountInfo.builder()
                                                .accountBalance(user.getAccountBalance())
                                                .accountNumber(user.getAccountNumber())
                                                .accountName(user.getFirstName() + " " + user.getLastName())
                                                .build())
                                .build();
        }

        @Override
        public String nameEnquiry(EnquiryRequest request) {

                if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
                        return AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE;
                }

                UserEntity user = userRepository.findByAccountNumber(request.getAccountNumber());

                return user.getFirstName() + " " + user.getLastName() + " " + user.getOthername();
        }

        // CREDIT
        @Override
        public BankResponce creditAccount(CreditDebitRequsest request) {

                if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
                        return BankResponce.builder()
                                        .responcecode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                                        .responcemessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                UserEntity user = userRepository.findByAccountNumber(request.getAccountNumber());

                user.setAccountBalance(user.getAccountBalance().add(request.getAmount()));
                userRepository.save(user);

                // Transaction log
                TransactionDto transactionDto = TransactionDto.builder()
                                .accountNumber(user.getAccountNumber())
                                .amount(request.getAmount())
                                .transactionType("CREDIT")
                                .status("SUCCESS")
                                .build();

                transactionService.saveTransaction(transactionDto);

                return BankResponce.builder()
                                .responcecode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                                .responcemessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                                .accountInfo(AccountInfo.builder()
                                                .accountName(user.getFirstName() + " " + user.getLastName())
                                                .accountNumber(user.getAccountNumber())
                                                .accountBalance(user.getAccountBalance())
                                                .build())
                                .build();
        }

        // DEBIT
        @Override
        public BankResponce debitAccount(CreditDebitRequsest request) {

                if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
                        return BankResponce.builder()
                                        .responcecode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                                        .responcemessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                UserEntity user = userRepository.findByAccountNumber(request.getAccountNumber());

                if (user.getAccountBalance().compareTo(request.getAmount()) < 0) {
                        return BankResponce.builder()
                                        .responcecode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                                        .responcemessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                user.setAccountBalance(user.getAccountBalance().subtract(request.getAmount()));
                userRepository.save(user);

                // Transaction log
                TransactionDto transactionDto = TransactionDto.builder()
                                .accountNumber(user.getAccountNumber())
                                .amount(request.getAmount())
                                .transactionType("DEBIT")
                                .status("SUCCESS")
                                .build();

                transactionService.saveTransaction(transactionDto);

                return BankResponce.builder()
                                .responcecode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                                .responcemessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                                .accountInfo(AccountInfo.builder()
                                                .accountName(user.getFirstName() + " " + user.getLastName())
                                                .accountNumber(user.getAccountNumber())
                                                .accountBalance(user.getAccountBalance())
                                                .build())
                                .build();
        }

        // TRANSFER
        @Transactional
        @Override
        public BankResponce transfer(TransferRequest request) {

                if (!userRepository.existsByAccountNumber(request.getSourceAccountNumber())) {
                        return BankResponce.builder()
                                        .responcecode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                                        .responcemessage("Source account not found")
                                        .accountInfo(null)
                                        .build();
                }

                if (!userRepository.existsByAccountNumber(request.getDestinationAccountNumber())) {
                        return BankResponce.builder()
                                        .responcecode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                                        .responcemessage("Destination account not found")
                                        .accountInfo(null)
                                        .build();
                }

                UserEntity source = userRepository.findByAccountNumber(request.getSourceAccountNumber());
                UserEntity destination = userRepository.findByAccountNumber(request.getDestinationAccountNumber());

                if (source.getAccountBalance().compareTo(request.getAmount()) < 0) {
                        return BankResponce.builder()
                                        .responcecode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                                        .responcemessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                                        .accountInfo(null)
                                        .build();
                }

                // Debit
                source.setAccountBalance(source.getAccountBalance().subtract(request.getAmount()));
                userRepository.save(source);

                // Credit
                destination.setAccountBalance(destination.getAccountBalance().add(request.getAmount()));
                userRepository.save(destination);

                // Transactions
                transactionService.saveTransaction(TransactionDto.builder()
                                .accountNumber(source.getAccountNumber())
                                .amount(request.getAmount())
                                .transactionType("TRANSFER_DEBIT")
                                .status("SUCCESS")
                                .build());

                transactionService.saveTransaction(TransactionDto.builder()
                                .accountNumber(destination.getAccountNumber())
                                .amount(request.getAmount())
                                .transactionType("TRANSFER_CREDIT")
                                .status("SUCCESS")
                                .build());

                // Emails
                emailService.sendEmailAlert(EmailDetails.builder()
                                .recipient(source.getEmail())
                                .subject("DEBIT ALERT")
                                .messageBody("Amount " + request.getAmount() +
                                                " debited. Current Balance: " + source.getAccountBalance())
                                .build());

                emailService.sendEmailAlert(EmailDetails.builder()
                                .recipient(destination.getEmail())
                                .subject("CREDIT ALERT")
                                .messageBody("Amount " + request.getAmount() +
                                                " credited. Current Balance: " + destination.getAccountBalance())
                                .build());

                return BankResponce.builder()
                                .responcecode(AccountUtils.TRANSFER_SUCCESS_CODE)
                                .responcemessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                                .accountInfo(null)
                                .build();
        }

        @Override
        public BankResponce login(LoginRequest request) {

                log.info("Login Attempt for Account: {}", request.getAccountNumber());

                UserEntity user = userRepository.findByAccountNumber(request.getAccountNumber());

                if (user == null) {
                        return BankResponce.builder()
                                        .responcecode(AccountUtils.ACCOUNT_NOT_FOUND_CODE)
                                        .responcemessage(AccountUtils.ACCOUNT_NOT_FOUND_MESSAGE)
                                        .build();
                }

                // Simple login validation: firstName matches username (case-insensitive)
                if (!user.getFirstName().equalsIgnoreCase(request.getUsername())) {
                        return BankResponce.builder()
                                        .responcecode("401")
                                        .responcemessage("Invalid Credentials")
                                        .build();
                }

                return BankResponce.builder()
                                .responcecode("200")
                                .responcemessage("Login Successful")
                                .accountInfo(AccountInfo.builder()
                                                .accountNumber(user.getAccountNumber())
                                                .accountName(user.getFirstName() + " " + user.getLastName())
                                                .accountBalance(user.getAccountBalance())
                                                .build())
                                .build();
        }
}