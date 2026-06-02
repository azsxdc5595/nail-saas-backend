
package com.nailsaas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    
    Optional<UserAccount> findByUserName(String userName);
    
    Optional<UserAccount> findByUserNameAndPassword(String userName, String password);
    
    void deleteByCode(String code);
    
    Optional<UserAccount> findByCode(String code);
    
    Optional<UserAccount> findByEmail(String email);
}
