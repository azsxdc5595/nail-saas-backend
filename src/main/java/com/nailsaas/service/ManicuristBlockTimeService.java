
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nailsaas.domain.ApplyManicuristBlockTimeRequest;
import com.nailsaas.domain.GetManicuristBlockTimeReponse;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.RemoveManicuristBlockTimeRequest;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.ManicuristBlockTime;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.ManicuristBlockTimeRepository;
import com.nailsaas.repository.ManicuristRepository;

@Service
public class ManicuristBlockTimeService {
    
    @Autowired
    private ManicuristRepository manicuristRepository;
    
    @Autowired
    private ManicuristBlockTimeRepository manicuristBlockTimeRepository;

    // 查詢美甲師的不可預約時間
    public GetManicuristBlockTimeReponse getBlockTime(GetManicuristInfoRequest req) {
        Optional<Manicurist> optionalManicurist = Optional.ofNullable(manicuristRepository.findByCode(req.getManicuristCode())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND)));
        Manicurist manicurist = optionalManicurist.get();
        List<ManicuristBlockTime> manicuristBlockTimeList = manicuristBlockTimeRepository.findByManicuristId(manicurist.getId());
        GetManicuristBlockTimeReponse reponse = GetManicuristBlockTimeReponse.builder().manicuristBlockTimeList(manicuristBlockTimeList).build();
        return reponse;
    }

    // 新增不可預約時間
    public void addBlockTime(ApplyManicuristBlockTimeRequest req) {
        ManicuristBlockTime blockTime = new ManicuristBlockTime();
        blockTime.setManicuristId(req.getManicuristId());
        blockTime.setStartTime(req.getStartTime());
        blockTime.setEndTime(req.getEndTime());
        blockTime.setBlockType(req.getBlockType());
        blockTime.setReason(req.getReason());
        blockTime.setCreateTime(LocalDateTime.now());
        manicuristBlockTimeRepository.save(blockTime);
    }
    
    // 移除不可預約時間
    public void removeBlockTime(RemoveManicuristBlockTimeRequest req) {
        manicuristBlockTimeRepository.deleteById(req.getBlockId());
    }
}