package com.nailsaas.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailsaas.domain.ManicuristWorkScheduleIntervalRequest;
import com.nailsaas.domain.ManicuristWorkScheduleIntervalResponse;
import com.nailsaas.domain.ManicuristWorkScheduleResponse;
import com.nailsaas.domain.ReplaceManicuristWorkScheduleRequest;
import com.nailsaas.entity.ManicuristWorkSchedule;
import com.nailsaas.entity.Reservation;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.ManicuristWorkScheduleRepository;
import com.nailsaas.repository.ReservationRepository;

@Service
public class ManicuristWorkScheduleService {

    private static final Set<String> TIME_OCCUPYING_RESERVATION_STATUSES = Set.of("PENDING", "CONFIRMED");

    private final ManicuristWorkScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final ManicuristRepository manicuristRepository;
    private final PermissionCheckService permissionCheckService;

    public ManicuristWorkScheduleService(
            ManicuristWorkScheduleRepository scheduleRepository,
            ReservationRepository reservationRepository,
            ManicuristRepository manicuristRepository,
            PermissionCheckService permissionCheckService) {
        this.scheduleRepository = scheduleRepository;
        this.reservationRepository = reservationRepository;
        this.manicuristRepository = manicuristRepository;
        this.permissionCheckService = permissionCheckService;
    }

    @Transactional(readOnly = true)
    public ManicuristWorkScheduleResponse getSchedule(LocalDate workDate) {
        Long manicuristId = permissionCheckService.getCurrentManicuristId();
        return toResponse(workDate, scheduleRepository
                .findByManicuristIdAndWorkDateOrderByStartMinuteOfDayAsc(manicuristId, workDate));
    }

    @Transactional
    public ManicuristWorkScheduleResponse replaceSchedule(
            LocalDate workDate,
            ReplaceManicuristWorkScheduleRequest request) {
        Long manicuristId = permissionCheckService.getCurrentManicuristId();
        manicuristRepository.findByIdForUpdate(manicuristId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND));

        List<ManicuristWorkScheduleIntervalRequest> intervals = request.getIntervals().stream()
                .sorted(Comparator.comparing(ManicuristWorkScheduleIntervalRequest::getStartMinuteOfDay))
                .toList();
        validateNoOverlappingIntervals(intervals);
        validateNoReservationConflicts(manicuristId, workDate, intervals);

        scheduleRepository.deleteByManicuristIdAndWorkDate(manicuristId, workDate);
        LocalDateTime now = LocalDateTime.now();
        List<ManicuristWorkSchedule> schedules = intervals.stream()
                .map(interval -> toEntity(manicuristId, workDate, interval, now))
                .toList();
        List<ManicuristWorkSchedule> saved = schedules.isEmpty()
                ? List.of()
                : scheduleRepository.saveAll(schedules);

        return toResponse(workDate, saved);
    }

    private void validateNoOverlappingIntervals(List<ManicuristWorkScheduleIntervalRequest> intervals) {
        for (int i = 1; i < intervals.size(); i++) {
            if (intervals.get(i).getStartMinuteOfDay() < intervals.get(i - 1).getEndMinuteOfDay()) {
                throw new BusinessException(ErrorCodeEnum.MANICURIST_SCHEDULE_INTERVAL_OVERLAP);
            }
        }
    }

    private void validateNoReservationConflicts(
            Long manicuristId,
            LocalDate workDate,
            List<ManicuristWorkScheduleIntervalRequest> proposedIntervals) {
        LocalDateTime dayStart = workDate.atStartOfDay();
        LocalDateTime dayEnd = workDate.plusDays(1).atStartOfDay();
        List<Reservation> reservations = reservationRepository
                .findByManicuristIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThanOrderByStartTimeAsc(
                        manicuristId,
                        TIME_OCCUPYING_RESERVATION_STATUSES,
                        dayEnd,
                        dayStart);

        boolean conflict = reservations.stream().anyMatch(reservation -> proposedIntervals.stream()
                .noneMatch(interval -> containsReservation(workDate, interval, reservation)));
        if (conflict) {
            throw new BusinessException(ErrorCodeEnum.MANICURIST_SCHEDULE_RESERVATION_CONFLICT);
        }
    }

    private boolean containsReservation(
            LocalDate workDate,
            ManicuristWorkScheduleIntervalRequest interval,
            Reservation reservation) {
        LocalDateTime intervalStart = workDate.atStartOfDay().plusMinutes(interval.getStartMinuteOfDay());
        LocalDateTime intervalEnd = workDate.atStartOfDay().plusMinutes(interval.getEndMinuteOfDay());
        return !reservation.getStartTime().isBefore(intervalStart)
                && !reservation.getEndTime().isAfter(intervalEnd);
    }

    private ManicuristWorkSchedule toEntity(
            Long manicuristId,
            LocalDate workDate,
            ManicuristWorkScheduleIntervalRequest interval,
            LocalDateTime now) {
        ManicuristWorkSchedule schedule = new ManicuristWorkSchedule();
        schedule.setManicuristId(manicuristId);
        schedule.setWorkDate(workDate);
        schedule.setStartMinuteOfDay(interval.getStartMinuteOfDay());
        schedule.setEndMinuteOfDay(interval.getEndMinuteOfDay());
        schedule.setCreateTime(now);
        schedule.setUpdateTime(now);
        return schedule;
    }

    private ManicuristWorkScheduleResponse toResponse(LocalDate workDate, List<ManicuristWorkSchedule> schedules) {
        List<ManicuristWorkScheduleIntervalResponse> intervals = schedules.stream()
                .sorted(Comparator.comparing(ManicuristWorkSchedule::getStartMinuteOfDay))
                .map(schedule -> new ManicuristWorkScheduleIntervalResponse(
                        schedule.getStartMinuteOfDay(),
                        schedule.getEndMinuteOfDay()))
                .toList();
        return new ManicuristWorkScheduleResponse(workDate, intervals);
    }
}
