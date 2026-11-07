
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailsaas.domain.ApplyReservationBlockTimeRequest;
import com.nailsaas.domain.GetReservationBlockTimeReponse;
import com.nailsaas.domain.RemoveReservationBlockTimeRequest;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.ReservationBlockTime;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.ReservationBlockTimeRepository;

@Service
public class ReservationBlockTimeService {
    
    private final ReservationBlockTimeRepository reservationBlockTimeRepository;

    private final PermissionCheckService permissionCheckService;

    ReservationBlockTimeService(PermissionCheckService permissionCheckService, ReservationBlockTimeRepository reservationBlockTimeRepository) {
        this.permissionCheckService = permissionCheckService;
        this.reservationBlockTimeRepository = reservationBlockTimeRepository;
    }

    // 查詢美甲師的不可預約時間
    public GetReservationBlockTimeReponse getBlockTime() {
        Manicurist manicurist = permissionCheckService.getCurrentManicurist();
        List<ReservationBlockTime> reservationBlockList = reservationBlockTimeRepository.findByManicuristId(manicurist.getId());
        GetReservationBlockTimeReponse reponse = GetReservationBlockTimeReponse.builder().reservationBlockTimeList(reservationBlockList).build();
        return reponse;
    }

    // 新增不可預約時間
    @Transactional
    public void addBlockTime(ApplyReservationBlockTimeRequest req) {
        Long manicuristId = permissionCheckService.getCurrentManicuristId();
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
        permissionCheckService.checkCurrentManicuristOwnership(blockTime.getManicuristId());
        reservationBlockTimeRepository.delete(blockTime);
    }
}
