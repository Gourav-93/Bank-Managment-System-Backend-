package com.TBMS.Bank.Managment.System.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TBMS.Bank.Managment.System.Entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsByEmail(String email);
    // Boolean existsByAccountNumber(String accountNumber);

    Boolean existsByAccountNumber(String accountNumber);

    UserEntity findByAccountNumber(String accountNumber);
}
