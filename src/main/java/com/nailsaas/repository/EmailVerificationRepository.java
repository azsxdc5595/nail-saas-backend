
package com.nailsaas.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nailsaas.entity.EmailVerification;

public interface EmailVerificationRepository extends CrudRepository<EmailVerification, Long> {
    
    Optional<EmailVerification> findTopByUserCodeAndEmailOrderByCreateTimeDesc(String userCode, String email);
    
    Optional<EmailVerification> findTopByEmailAndStatusOrderByCreateTimeDesc(String email, String stutas);
    
    Optional<EmailVerification> findTopByUserCodeAndEmailAndStatusOrderByCreateTimeDesc(String userCode, String email, String stutas);

    void deleteByEmail(String email);
}
