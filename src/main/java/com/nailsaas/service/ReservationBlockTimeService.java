
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void addBlockTime(ApplyReservationBlockTimeRequest req) {
        ReservationBlockTime blockTime = new ReservationBlockTime();
        blockTime.setManicuristId(req.getManicuristId());
        blockTime.setStartTime(req.getStartTime());
        blockTime.setEndTime(req.getEndTime());
        blockTime.setBlockType(req.getBlockType());
        blockTime.setReason(req.getReason());
        blockTime.setCreateTime(LocalDateTime.now());
        reservationBlockTimeRepository.save(blockTime);
    }
    
    // 移除不可預約時間
    public void removeBlockTime(RemoveReservationBlockTimeRequest req) {
        reservationBlockTimeRepository.deleteById(req.getBlockId());
    }
}