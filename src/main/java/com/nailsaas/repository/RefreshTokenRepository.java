
package com.nailsaas.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.nailsaas.entity.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserCode(String userCode);
}
