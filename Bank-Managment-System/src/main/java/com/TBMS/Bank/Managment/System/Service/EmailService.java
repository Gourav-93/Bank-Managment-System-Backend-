package com.TBMS.Bank.Managment.System.Service;

import com.TBMS.Bank.Managment.System.Dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}