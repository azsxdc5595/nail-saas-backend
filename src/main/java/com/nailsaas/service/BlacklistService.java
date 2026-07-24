
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nailsaas.domain.BlacklistRequest;
import com.nailsaas.entity.ManicuristBlacklist;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.BlacklistRepository;

@Service
public class BlacklistService {

    @Autowired
    private BlacklistRepository blacklistRepository;

    // 查詢
    public List<ManicuristBlacklist> getBlacklistByManicuristId(BlacklistRequest req) {
        return blacklistRepository.findByManicuristId(req.getManicuristId());
    }
    
    // 增至黑名單
    @Transactional
    public String addToBlacklist(BlacklistRequest req) {

        // 先檢查是否已存在
        boolean exists = blacklistRepository.existsByManicuristIdAndUserId(req.getManicuristId(), req.getUserId());

        if (exists) {
            throw new BusinessException(ErrorCodeEnum.BLACKLIST_ALREADY_EXISTS);
        }

        // 建立 entity
        ManicuristBlacklist entity = new ManicuristBlacklist();

        entity.setManicuristId(req.getManicuristId()); // 美甲師ID
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
                req.getManicuristId(),
                req.getUserId()
        );

        if (n == 0) {
            throw new BusinessException(ErrorCodeEnum.BLACKLIST_NOT_FOUND);
        }

        return "移除成功";
    }

}