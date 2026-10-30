
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailsaas.domain.ApplyReservationBlockTimeRequest;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.GetReservationBlockTimeReponse;
import com.nailsaas.domain.RemoveReservationBlockTimeRequest;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.ReservationBlockTime;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.ReservationBlockTimeRepository;
import com.nailsaas.util.SecurityUtil;

@Service
public class ReservationBlockTimeService {
    
    @Autowired
    private ManicuristRepository manicuristRepository;
    
    @Autowired
    private ReservationBlockTimeRepository reservationBlockTimeRepository;

    // 查詢美甲師的不可預約時間
    public GetReservationBlockTimeReponse getBlockTime(GetManicuristInfoRequest req) {
        Optional<Manicurist> optionalManicurist = Optional.ofNullable(manicuristRepository.findByCode(req.getManicuristCode())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND)));
        Manicurist manicurist = optionalManicurist.get();
        List<ReservationBlockTime> reservationBlockList = reservationBlockTimeRepository.findByManicuristId(manicurist.getId());
        GetReservationBlockTimeReponse reponse = GetReservationBlockTimeReponse.builder().reservationBlockTimeList(reservationBlockList).build();
        return reponse;
    }

    // 新增不可預約時間
    @Transactional
    public void addBlockTime(ApplyReservationBlockTimeRequest req) {
        Long manicuristId = getCurrentManicuristId();
        ReservationBlockTime blockTime = new ReservationBlockTime();
        blockTime.setManicuristId(manicuristId);
        blockTime.setStartTime(req.getStartTime());
        blockTime.setEndTime(req.getEndTime());
        blockTime.setBlockType(req.getBlockType());
        blockTime.setReason(req.getReason());
        blockTime.setCreateTime(LocalDateTime.now());
        reservationBlockTimeRepository.save(blockTime);
    }
    
    // 移除不可預約時間
    @Transactional
    public void removeBlockTime(RemoveReservationBlockTimeRequest req) {
        ReservationBlockTime blockTime = reservationBlockTimeRepository.findById(req.getBlockId())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.RESERVATION_BLOCK_NOT_FOUND));
        Long manicuristId = getCurrentManicuristId();
        if (!Objects.equals(blockTime.getManicuristId(), manicuristId)) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED);
        }
        reservationBlockTimeRepository.delete(blockTime);
    }

    private Long getCurrentManicuristId() {
        Long manicuristId = manicuristRepository.searchManicuristIdByUserCode(SecurityUtil.getCurrentUserCode());
        if (manicuristId == null) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED);
        }
        return manicuristId;
    }
}
