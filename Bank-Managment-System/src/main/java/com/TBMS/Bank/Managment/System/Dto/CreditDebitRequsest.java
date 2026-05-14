package com.TBMS.Bank.Managment.System.Dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDebitRequsest {

    @NotBlank
    private String accountNumber;

    @NotBlank
    private BigDecimal amount;

}
