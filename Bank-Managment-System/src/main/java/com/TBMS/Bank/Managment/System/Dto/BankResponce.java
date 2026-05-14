package com.TBMS.Bank.Managment.System.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankResponce {

    private String responcecode;
    private String responcemessage;
    private AccountInfo accountInfo;

}
