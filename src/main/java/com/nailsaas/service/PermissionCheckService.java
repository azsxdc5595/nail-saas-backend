package com.nailsaas.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.UserAccount;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.UserAccountRepository;
import com.nailsaas.util.SecurityUtil;

/** Shared resolution and ownership checks for the authenticated account. */
@Service
public class PermissionCheckService {

    private final UserAccountRepository userAccountRepository;
    private final ManicuristRepository manicuristRepository;

    public PermissionCheckService(UserAccountRepository userAccountRepository,
            ManicuristRepository manicuristRepository) {
        this.userAccountRepository = userAccountRepository;
        this.manicuristRepository = manicuristRepository;
    }

    public UserAccount getCurrentUser() {
        return userAccountRepository.findByCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.USER_NOT_FOUND));
    }

    public Manicurist getCurrentManicurist() {
        return getCurrentManicurist(ErrorCodeEnum.PERMISSION_DENIED);
    }

    public Manicurist getCurrentManicurist(ErrorCodeEnum missingManicuristError) {
        return manicuristRepository.findManicuristByUserCode(SecurityUtil.getCurrentUserCode())
                .orElseThrow(() -> new BusinessException(missingManicuristError));
    }

    public Long getCurrentManicuristId() {
        Long manicuristId = manicuristRepository.searchManicuristIdByUserCode(SecurityUtil.getCurrentUserCode());
        if (manicuristId == null) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED);
        }
        return manicuristId;
    }

    public void checkManicuristOwnership(Long currentManicuristId, Long resourceManicuristId) {
        if (!Objects.equals(currentManicuristId, resourceManicuristId)) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED);
        }
    }

    public void checkCurrentManicuristOwnership(Long resourceManicuristId) {
        checkManicuristOwnership(getCurrentManicuristId(), resourceManicuristId);
    }
}
