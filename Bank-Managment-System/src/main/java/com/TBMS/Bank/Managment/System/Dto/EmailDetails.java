package com.TBMS.Bank.Managment.System.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

    private String recipient;    
    private String messageBody;
    private String subject;

    public String getSubject() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}