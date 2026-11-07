
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailsaas.domain.BlacklistRequest;
import com.nailsaas.entity.ManicuristBlacklist;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.BlacklistRepository;

@Service
public class BlacklistService {

    private final BlacklistRepository blacklistRepository;

    private final PermissionCheckService permissionCheckService;

    BlacklistService(BlacklistRepository blacklistRepository, PermissionCheckService permissionCheckService) {
        this.blacklistRepository = blacklistRepository;
        this.permissionCheckService = permissionCheckService;
    }

    // 查詢
    public List<ManicuristBlacklist> getBlacklistByCurrentManicurist() {
        return blacklistRepository.findByManicuristId(permissionCheckService.getCurrentManicuristId());
    }
    
    // 增至黑名單
    @Transactional
    public String addToBlacklist(BlacklistRequest req) {

        // 先檢查是否已存在
        Long manicuristId = permissionCheckService.getCurrentManicuristId();
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
                permissionCheckService.getCurrentManicuristId(),
                req.getUserId()
        );

        if (n == 0) {
            throw new BusinessException(ErrorCodeEnum.BLACKLIST_NOT_FOUND);
        }

        return "移除成功";
    }

}
