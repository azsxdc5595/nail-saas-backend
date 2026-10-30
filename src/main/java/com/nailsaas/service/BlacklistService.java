
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailsaas.domain.BlacklistRequest;
import com.nailsaas.entity.ManicuristBlacklist;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.BlacklistRepository;
import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.util.SecurityUtil;

@Service
public class BlacklistService {

    @Autowired
    private BlacklistRepository blacklistRepository;

    @Autowired
    private ManicuristRepository manicuristRepository;

    // 查詢
    public List<ManicuristBlacklist> getBlacklistByCurrentManicurist() {
        return blacklistRepository.findByManicuristId(getCurrentManicuristId());
    }
    
    // 增至黑名單
    @Transactional
    public String addToBlacklist(BlacklistRequest req) {

        // 先檢查是否已存在
        Long manicuristId = getCurrentManicuristId();
        boolean exists = blacklistRepository.existsByManicuristIdAndUserId(manicuristId, req.getUserId());

        if (exists) {
            throw new BusinessException(ErrorCodeEnum.BLACKLIST_ALREADY_EXISTS);
        }

        // 建立 entity
        ManicuristBlacklist entity = new ManicuristBlacklist();

        entity.setManicuristId(manicuristId);
        entity.setUserId(req.getUserId());             // 被封鎖使用者ID
        entity.setReason(req.getReason());             // 原因
        entity.setCreateTime(LocalDateTime.now());     // 建立時間

        // 寫入 DB
        blacklistRepository.save(entity);
        return "已將此用戶加入黑名單";
    }
    
    @Transactional
    public String removeFromBlacklist(BlacklistRequest req) {

        int n = blacklistRepository.deleteByManicuristIdAndUserId(
                getCurrentManicuristId(),
                req.getUserId()
        );

        if (n == 0) {
            throw new BusinessException(ErrorCodeEnum.BLACKLIST_NOT_FOUND);
        }

        return "移除成功";
    }

    private Long getCurrentManicuristId() {
        Long manicuristId = manicuristRepository.searchManicuristIdByUserCode(SecurityUtil.getCurrentUserCode());
        if (manicuristId == null) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED);
        }
        return manicuristId;
    }

}
