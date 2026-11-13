package com.nailsaas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nailsaas.domain.CreateManicuristReservationRequest;
import com.nailsaas.domain.CreateMemberReservationRequest;
import com.nailsaas.domain.ProposeReservationTimeRequest;
import com.nailsaas.domain.ProposeReservationServicesRequest;
import com.nailsaas.domain.ReservationAvailabilityResponse;
import com.nailsaas.domain.ReservationResponse;
import com.nailsaas.domain.ReservationServiceResponse;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.entity.ManicuristWorkSchedule;
import com.nailsaas.entity.Reservation;
import com.nailsaas.entity.ReservationItem;
import com.nailsaas.entity.ServiceItem;
import com.nailsaas.entity.UserAccount;
import com.nailsaas.enums.ErrorCodeEnum;
import com.nailsaas.enums.ReservationStatusEnum;
import com.nailsaas.exception.BusinessException;
import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.ManicuristWorkScheduleRepository;
import com.nailsaas.repository.ReservationItemRepository;
import com.nailsaas.repository.ReservationRepository;
import com.nailsaas.repository.ServiceItemRepository;
import com.nailsaas.repository.UserAccountRepository;
import com.nailsaas.repository.UserRoleRepository;

@Service
public class ReservationService {

    private static final String MEMBER = "MEMBER";
    private static final String MANICURIST = "MANICURIST";
    private static final int RESPONSE_WINDOW_HOURS = 24;
    private static final int START_TIME_STEP_MINUTES = 30;
    private static final Set<ReservationStatusEnum> SLOT_OCCUPYING_STATUSES =
            Set.of(ReservationStatusEnum.PENDING, ReservationStatusEnum.CONFIRMED);

    private final ReservationRepository reservationRepository;
    private final ReservationItemRepository reservationItemRepository;
    private final ManicuristRepository manicuristRepository;
    private final ManicuristWorkScheduleRepository scheduleRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionCheckService permissionCheckService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationItemRepository reservationItemRepository,
            ManicuristRepository manicuristRepository,
            ManicuristWorkScheduleRepository scheduleRepository,
            ServiceItemRepository serviceItemRepository,
            UserAccountRepository userAccountRepository,
            UserRoleRepository userRoleRepository,
            PermissionCheckService permissionCheckService) {
        this.reservationRepository = reservationRepository;
        this.reservationItemRepository = reservationItemRepository;
        this.manicuristRepository = manicuristRepository;
        this.scheduleRepository = scheduleRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.userAccountRepository = userAccountRepository;
        this.userRoleRepository = userRoleRepository;
        this.permissionCheckService = permissionCheckService;
    }

    @Transactional
    public ReservationResponse createByMember(CreateMemberReservationRequest request) {
        UserAccount member = permissionCheckService.getCurrentUser();
        Manicurist manicurist = lockManicurist(request.getManicuristId());
        requireManicuristActive(manicurist);
        if (manicurist.getUserId().equals(member.getId())) {
            throw new BusinessException(ErrorCodeEnum.PERMISSION_DENIED);
        }

        ServiceTotals totals = calculateServices(request.getServiceIds(), manicurist.getId());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = request.getStartTime().plusMinutes(totals.durationMin());
        validateSlot(manicurist, request.getStartTime(), endTime, now, null);

        Reservation reservation = new Reservation();
        reservation.setCode(UUID.randomUUID().toString());
        reservation.setShopId(manicurist.getShopId());
        reservation.setUserId(member.getId());
        reservation.setManicuristId(manicurist.getId());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(endTime);
        reservation.setStatus(ReservationStatusEnum.PENDING);
        reservation.setInitiatorType(MEMBER);
        reservation.setTotalDurationMin(totals.durationMin());
        reservation.setTotalPrice(totals.price());
        reservation.setCustomerNote(request.getCustomerNote());
        reservation.setCreateTime(now);
        reservation.setUpdateTime(now);
        Reservation saved = reservationRepository.save(reservation);
        List<ReservationItem> details = createReservationItems(saved.getId(), totals.items(), now);
        reservationItemRepository.saveAll(details);
        return toResponse(saved, details.stream().map(this::toServiceResponse).toList());
    }

    @Transactional
    public ReservationResponse createByManicurist(CreateManicuristReservationRequest request) {
        Manicurist currentManicurist = permissionCheckService.getCurrentManicurist();
        Manicurist manicurist = lockManicurist(currentManicurist.getId());
        requireManicuristActive(manicurist);
        UserAccount member = userAccountRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.USER_NOT_FOUND));
        if (member.getId().equals(manicurist.getUserId())
                || !userRoleRepository.existsByUserIdAndRoleCodeAndIsActive(member.getId(), MEMBER, 1)) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_MEMBER_ROLE_REQUIRED);
        }

        ServiceTotals totals = calculateServices(request.getServiceIds(), manicurist.getId());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = request.getStartTime().plusMinutes(totals.durationMin());
        validateSlot(manicurist, request.getStartTime(), endTime, now, null);

        Reservation reservation = new Reservation();
        reservation.setCode(UUID.randomUUID().toString());
        reservation.setShopId(manicurist.getShopId());
        reservation.setUserId(member.getId());
        reservation.setManicuristId(manicurist.getId());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(endTime);
        reservation.setStatus(ReservationStatusEnum.PENDING);
        reservation.setInitiatorType(MANICURIST);
        reservation.setTotalDurationMin(totals.durationMin());
        reservation.setTotalPrice(totals.price());
        reservation.setManicuristNote(request.getManicuristNote());
        reservation.setCreateTime(now);
        reservation.setUpdateTime(now);
        Reservation saved = reservationRepository.save(reservation);
        List<ReservationItem> details = createReservationItems(saved.getId(), totals.items(), now);
        reservationItemRepository.saveAll(details);
        return toResponse(saved, details.stream().map(this::toServiceResponse).toList());
    }

    @Transactional(readOnly = true)
    public ReservationAvailabilityResponse getAvailability(Long manicuristId, LocalDate workDate,
            List<Long> serviceIds, Long reservationId) {
        Manicurist manicurist = manicuristRepository.findById(manicuristId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND));
        requireManicuristActive(manicurist);
        int serviceDurationMin;
        Long excludedReservationId = null;
        if (reservationId == null) {
            serviceDurationMin = calculateServices(serviceIds, manicuristId).durationMin();
        } else {
            Reservation currentReservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new BusinessException(ErrorCodeEnum.RESERVATION_NOT_FOUND));
            requireParticipant(currentReservation);
            if (!manicuristId.equals(currentReservation.getManicuristId())
                    || currentReservation.getStatus() != ReservationStatusEnum.PENDING
                    || isResponseExpired(currentReservation, LocalDateTime.now())) {
                throw new BusinessException(ErrorCodeEnum.RESERVATION_INVALID_STATUS);
            }
            if (serviceIds != null && !matchesReservationServices(reservationId, serviceIds)) {
                throw new BusinessException(ErrorCodeEnum.RESERVATION_SERVICE_INVALID);
            }
            serviceDurationMin = currentReservation.getTotalDurationMin();
            excludedReservationId = currentReservation.getId();
        }
        final Long reservationToExclude = excludedReservationId;
        LocalDateTime dayStart = workDate.atStartOfDay();
        LocalDateTime dayEnd = workDate.plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = reservationRepository
                .findByManicuristIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        manicuristId, SLOT_OCCUPYING_STATUSES, dayEnd, dayStart)
                .stream()
                .filter(reservation -> !reservation.getId().equals(reservationToExclude))
                .filter(reservation -> reservation.getStatus() == ReservationStatusEnum.CONFIRMED
                        || !isResponseExpired(reservation, now))
                .toList();

        List<ManicuristWorkSchedule> windows = scheduleRepository
                .findByManicuristIdAndWorkDateOrderByStartMinuteOfDayAsc(manicuristId, workDate);
        List<LocalDateTime> available = new ArrayList<>();
        for (MinuteRange window : mergeScheduleWindows(windows)) {
            List<MinuteRange> occupiedRanges = reservations.stream()
                    .filter(reservation -> SLOT_OCCUPYING_STATUSES.contains(reservation.getStatus()))
                    .filter(reservation -> reservation.getStartTime().isBefore(
                            dayStart.plusMinutes(window.endMinute())))
                    .filter(reservation -> reservation.getEndTime().isAfter(
                            dayStart.plusMinutes(window.startMinute())))
                    .map(reservation -> new MinuteRange(
                            Math.max(window.startMinute(), startMinuteOnDate(
                                    workDate, reservation.getStartTime())),
                            Math.min(window.endMinute(), endMinuteOnDate(
                                    workDate, reservation.getEndTime()))))
                    .sorted((left, right) -> Integer.compare(left.startMinute(), right.startMinute()))
                    .toList();

            int freeStartMinute = window.startMinute();
            for (MinuteRange occupiedRange : occupiedRanges) {
                if (occupiedRange.startMinute() > freeStartMinute) {
                    addAvailableStartTimes(available, dayStart, workDate, freeStartMinute,
                            occupiedRange.startMinute(), serviceDurationMin, now);
                }
                freeStartMinute = Math.max(freeStartMinute, occupiedRange.endMinute());
            }
            if (freeStartMinute < window.endMinute()) {
                addAvailableStartTimes(available, dayStart, workDate, freeStartMinute,
                        window.endMinute(), serviceDurationMin, now);
            }
        }
        return new ReservationAvailabilityResponse(manicuristId, workDate, serviceDurationMin, available);
    }

    private boolean matchesReservationServices(Long reservationId, List<Long> serviceIds) {
        if (serviceIds.isEmpty() || serviceIds.size() > 20
                || new HashSet<>(serviceIds).size() != serviceIds.size()) {
            return false;
        }
        List<Long> currentServiceIds = reservationItemRepository
                .findByReservationIdOrderByServiceSeqAsc(reservationId)
                .stream()
                .map(ReservationItem::getServiceId)
                .toList();
        return currentServiceIds.size() == serviceIds.size()
                && new HashSet<>(currentServiceIds).equals(new HashSet<>(serviceIds));
    }

    private void addAvailableStartTimes(
            List<LocalDateTime> available,
            LocalDateTime dayStart,
            LocalDate workDate,
            int freeStartMinute,
            int freeEndMinute,
            int serviceDurationMin,
            LocalDateTime now) {
        int candidateMinute = firstHalfHourAtOrAfter(freeStartMinute);
        while (candidateMinute + serviceDurationMin <= freeEndMinute) {
            LocalDateTime startTime = dayStart.plusMinutes(candidateMinute);
            if (startTime.isAfter(now)) {
                available.add(startTime);
            }
            candidateMinute += START_TIME_STEP_MINUTES;
        }
    }

    @Transactional
    public Page<ReservationResponse> getMine(Pageable pageable) {
        UserAccount user = permissionCheckService.getCurrentUser();
        Long manicuristId = manicuristRepository.findByUserId(user.getId())
                .map(Manicurist::getId)
                .orElse(null);
        Page<Reservation> page = reservationRepository
                .findByUserIdOrManicuristIdOrderByStartTimeDesc(user.getId(), manicuristId, pageable);
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> reservations = new ArrayList<>(page.getContent());
        for (int i = 0; i < reservations.size(); i++) {
            Reservation reservation = reservations.get(i);
            if (reservation.getStatus() == ReservationStatusEnum.PENDING
                    && isResponseExpired(reservation, now)) {
                Reservation locked = reservationRepository.findByIdForUpdate(reservation.getId())
                        .orElse(reservation);
                expireIfNeeded(locked, now);
                reservationRepository.save(locked);
                reservations.set(i, locked);
            }
        }

        List<Long> ids = reservations.stream().map(Reservation::getId).toList();
        Map<Long, List<ReservationItem>> itemsByReservation = ids.isEmpty()
                ? Map.of()
                : reservationItemRepository.findByReservationIdInOrderByReservationIdAscServiceSeqAsc(ids)
                        .stream()
                        .collect(Collectors.groupingBy(ReservationItem::getReservationId));
        List<ReservationResponse> responses = reservations.stream().map(reservation -> toResponse(
                reservation,
                itemsByReservation.getOrDefault(reservation.getId(), List.of()).stream()
                        .map(this::toServiceResponse)
                        .toList()))
                .toList();
        return new PageImpl<>(responses, pageable, page.getTotalElements());
    }

    @Transactional
    public ReservationResponse getById(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.RESERVATION_NOT_FOUND));
        requireParticipant(reservation);
        boolean expired = expireIfNeeded(reservation, LocalDateTime.now());
        if (expired) {
            reservationRepository.save(reservation);
        }
        return toResponse(reservation, getServiceResponses(reservation.getId()));
    }

    @Transactional
    public ReservationResponse confirm(Long reservationId) {
        Reservation reservation = lockReservationAndManicurist(reservationId);
        if (expireIfNeeded(reservation, LocalDateTime.now())) {
            return toResponse(reservationRepository.save(reservation), getServiceResponses(reservationId));
        }
        requirePending(reservation);
        requireRespondingParty(reservation);

        LocalDateTime now = LocalDateTime.now();
        reservation.setStatus(ReservationStatusEnum.CONFIRMED);
        reservation.setUpdateTime(now);
        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved, getServiceResponses(reservationId));
    }

    @Transactional
    public ReservationResponse reject(Long reservationId) {
        Reservation reservation = lockReservationAndManicurist(reservationId);
        if (expireIfNeeded(reservation, LocalDateTime.now())) {
            return toResponse(reservationRepository.save(reservation), getServiceResponses(reservationId));
        }
        requirePending(reservation);
        requireRespondingParty(reservation);
        reservation.setStatus(ReservationStatusEnum.REJECTED);
        reservation.setUpdateTime(LocalDateTime.now());
        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved, getServiceResponses(reservationId));
    }

    @Transactional
    public ReservationResponse proposeTime(Long reservationId, ProposeReservationTimeRequest request) {
        Reservation reservation = lockReservationAndManicurist(reservationId);
        if (expireIfNeeded(reservation, LocalDateTime.now())) {
            return toResponse(reservationRepository.save(reservation), getServiceResponses(reservationId));
        }
        requirePending(reservation);
        if (!MEMBER.equals(reservation.getInitiatorType())) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_INVALID_STATUS);
        }
        Manicurist actor = permissionCheckService.getCurrentManicurist();
        if (!actor.getId().equals(reservation.getManicuristId())) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_NOT_PARTICIPANT);
        }

        Manicurist manicurist = manicuristRepository.findById(reservation.getManicuristId())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newEndTime = request.getStartTime().plusMinutes(reservation.getTotalDurationMin());
        validateSlot(manicurist, request.getStartTime(), newEndTime, now, reservation.getId());

        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(newEndTime);
        reservation.setInitiatorType(MANICURIST);
        reservation.setUpdateTime(now);
        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved, getServiceResponses(reservationId));
    }

    @Transactional
    public ReservationResponse proposeServices(Long reservationId, ProposeReservationServicesRequest request) {
        Reservation reservation = lockReservationAndManicurist(reservationId);
        if (expireIfNeeded(reservation, LocalDateTime.now())) {
            return toResponse(reservationRepository.save(reservation), getServiceResponses(reservationId));
        }
        requirePending(reservation);
        Manicurist actor = permissionCheckService.getCurrentManicurist();
        if (!actor.getId().equals(reservation.getManicuristId())) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_NOT_PARTICIPANT);
        }

        Manicurist manicurist = manicuristRepository.findById(reservation.getManicuristId())
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND));
        ServiceTotals totals = calculateServices(request.getServiceIds(), manicurist.getId());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = reservation.getStartTime().plusMinutes(totals.durationMin());
        validateSlot(manicurist, reservation.getStartTime(), endTime, now, reservation.getId());

        reservationItemRepository.deleteByReservationId(reservationId);
        List<ReservationItem> serviceItems = createReservationItems(reservationId, totals.items(), now);
        reservationItemRepository.saveAll(serviceItems);
        reservation.setEndTime(endTime);
        reservation.setTotalDurationMin(totals.durationMin());
        reservation.setTotalPrice(totals.price());
        reservation.setInitiatorType(MANICURIST);
        reservation.setUpdateTime(now);
        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved, serviceItems.stream().map(this::toServiceResponse).toList());
    }

    @Transactional
    public ReservationResponse cancel(Long reservationId) {
        Reservation reservation = lockReservationAndManicurist(reservationId);
        requireParticipant(reservation);
        if (expireIfNeeded(reservation, LocalDateTime.now())) {
            return toResponse(reservationRepository.save(reservation), getServiceResponses(reservationId));
        }
        if (reservation.getStatus() != ReservationStatusEnum.PENDING
                && reservation.getStatus() != ReservationStatusEnum.CONFIRMED) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_INVALID_STATUS);
        }
        reservation.setStatus(ReservationStatusEnum.CANCELLED);
        reservation.setUpdateTime(LocalDateTime.now());
        return toResponse(reservationRepository.save(reservation), getServiceResponses(reservation.getId()));
    }

    @Transactional
    public ReservationResponse finish(Long reservationId) {
        Reservation reservation = lockReservationAndManicurist(reservationId);
        requireCurrentManicurist(reservation);
        if (reservation.getStatus() != ReservationStatusEnum.CONFIRMED
                || LocalDateTime.now().isBefore(reservation.getEndTime())) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_INVALID_STATUS);
        }
        reservation.setStatus(ReservationStatusEnum.FINISH);
        reservation.setUpdateTime(LocalDateTime.now());
        return toResponse(reservationRepository.save(reservation), getServiceResponses(reservation.getId()));
    }

    @Transactional
    public ReservationResponse markNoShow(Long reservationId) {
        Reservation reservation = lockReservationAndManicurist(reservationId);
        requireCurrentManicurist(reservation);
        if (reservation.getStatus() != ReservationStatusEnum.CONFIRMED
                || LocalDateTime.now().isBefore(reservation.getStartTime())) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_INVALID_STATUS);
        }
        reservation.setStatus(ReservationStatusEnum.NO_SHOW);
        reservation.setUpdateTime(LocalDateTime.now());
        return toResponse(reservationRepository.save(reservation), getServiceResponses(reservation.getId()));
    }

    private Manicurist lockManicurist(Long manicuristId) {
        return manicuristRepository.findByIdForUpdate(manicuristId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND));
    }

    private void requireManicuristActive(Manicurist manicurist) {
        if (!"ACTIVE".equals(manicurist.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.MANICURIST_NOT_FOUND);
        }
    }

    private Reservation lockReservationAndManicurist(Long reservationId) {
        Reservation snapshot = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.RESERVATION_NOT_FOUND));
        lockManicurist(snapshot.getManicuristId());
        return reservationRepository.findByIdForUpdate(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.RESERVATION_NOT_FOUND));
    }

    private ServiceTotals calculateServices(List<Long> serviceIds, Long manicuristId) {
        if (serviceIds == null || serviceIds.isEmpty() || serviceIds.size() > 20
                || new HashSet<>(serviceIds).size() != serviceIds.size()) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_SERVICE_INVALID);
        }
        List<ServiceItem> found = serviceItemRepository.findAllById(serviceIds);
        Map<Long, ServiceItem> byId = found.stream()
                .collect(Collectors.toMap(ServiceItem::getId, Function.identity()));
        if (byId.size() != serviceIds.size()) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_SERVICE_INVALID);
        }

        List<ServiceItem> orderedItems = new ArrayList<>(serviceIds.size());
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalDuration = 0;
        for (Long serviceId : serviceIds) {
            ServiceItem item = byId.get(serviceId);
            if (!manicuristId.equals(item.getManicuristId())
                    || !Integer.valueOf(1).equals(item.getIsActive())
                    || item.getPrice() == null
                    || item.getDurationMin() == null
                    || item.getDurationMin() <= 0) {
                throw new BusinessException(ErrorCodeEnum.RESERVATION_SERVICE_INVALID);
            }
            orderedItems.add(item);
            totalPrice = totalPrice.add(item.getPrice());
            try {
                totalDuration = Math.addExact(totalDuration, item.getDurationMin());
            } catch (ArithmeticException ex) {
                throw new BusinessException(ErrorCodeEnum.RESERVATION_SERVICE_INVALID);
            }
        }
        if (totalDuration > 99_999 || totalPrice.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_SERVICE_INVALID);
        }
        return new ServiceTotals(List.copyOf(orderedItems), totalPrice, totalDuration);
    }

    private void validateSlot(
            Manicurist manicurist,
            LocalDateTime startTime,
            LocalDateTime endTime,
            LocalDateTime now,
            Long excludedReservationId) {
        if (startTime == null || endTime == null || !startTime.isAfter(now)
                || startTime.getMinute() % START_TIME_STEP_MINUTES != 0
                || startTime.getSecond() != 0 || startTime.getNano() != 0
                || !endTime.isAfter(startTime)) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_TIME_INVALID);
        }

        LocalDate workDate = startTime.toLocalDate();
        int startMinute = startTime.getHour() * 60 + startTime.getMinute();
        int endMinute = toMinuteOfWorkDate(workDate, endTime);
        List<ManicuristWorkSchedule> windows = scheduleRepository
                .findByManicuristIdAndWorkDateOrderByStartMinuteOfDayAsc(manicurist.getId(), workDate);
        boolean withinWindow = mergeScheduleWindows(windows).stream().anyMatch(window ->
                startMinute >= window.startMinute() && endMinute <= window.endMinute());
        if (!withinWindow) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_OUTSIDE_SCHEDULE);
        }

        List<Reservation> conflicts = reservationRepository
                .findByManicuristIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        manicurist.getId(), SLOT_OCCUPYING_STATUSES, endTime, startTime);
        boolean conflict = false;
        for (Reservation reservation : conflicts) {
            if (excludedReservationId != null && excludedReservationId.equals(reservation.getId())) {
                continue;
            }
            if (expireIfNeeded(reservation, now)) {
                reservationRepository.save(reservation);
            } else {
                conflict = true;
            }
        }
        if (conflict) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_TIME_CONFLICT);
        }
    }

    private int toMinuteOfWorkDate(LocalDate workDate, LocalDateTime endTime) {
        if (endTime.toLocalDate().equals(workDate)) {
            return endTime.getHour() * 60 + endTime.getMinute();
        }
        if (endTime.equals(workDate.plusDays(1).atStartOfDay())) {
            return 1440;
        }
        throw new BusinessException(ErrorCodeEnum.RESERVATION_OUTSIDE_SCHEDULE);
    }

    private int startMinuteOnDate(LocalDate workDate, LocalDateTime startTime) {
        return startTime.toLocalDate().isBefore(workDate)
                ? 0
                : toMinuteOfWorkDate(workDate, startTime);
    }

    private int endMinuteOnDate(LocalDate workDate, LocalDateTime endTime) {
        return endTime.toLocalDate().isAfter(workDate)
                ? 1440
                : toMinuteOfWorkDate(workDate, endTime);
    }

    private List<MinuteRange> mergeScheduleWindows(List<ManicuristWorkSchedule> schedules) {
        List<MinuteRange> merged = new ArrayList<>();
        for (ManicuristWorkSchedule schedule : schedules) {
            MinuteRange next = new MinuteRange(
                    schedule.getStartMinuteOfDay(), schedule.getEndMinuteOfDay());
            if (merged.isEmpty()) {
                merged.add(next);
                continue;
            }
            MinuteRange current = merged.get(merged.size() - 1);
            if (next.startMinute() <= current.endMinute()) {
                merged.set(merged.size() - 1, new MinuteRange(
                        current.startMinute(), Math.max(current.endMinute(), next.endMinute())));
            } else {
                merged.add(next);
            }
        }
        return merged;
    }

    private int firstHalfHourAtOrAfter(int minuteOfDay) {
        int remainder = minuteOfDay % START_TIME_STEP_MINUTES;
        return remainder == 0 ? minuteOfDay : minuteOfDay + START_TIME_STEP_MINUTES - remainder;
    }

    private boolean expireIfNeeded(Reservation reservation, LocalDateTime now) {
        if (reservation.getStatus() != ReservationStatusEnum.PENDING) {
            return false;
        }
        if (!isResponseExpired(reservation, now)) {
            return false;
        }
        reservation.setStatus(ReservationStatusEnum.EXPIRED);
        reservation.setUpdateTime(now);
        return true;
    }

    private boolean isResponseExpired(Reservation reservation, LocalDateTime now) {
        LocalDateTime lastActionTime = reservation.getUpdateTime() != null
                ? reservation.getUpdateTime()
                : reservation.getCreateTime();
        return lastActionTime != null && !now.isBefore(lastActionTime.plusHours(RESPONSE_WINDOW_HOURS));
    }

    private void requirePending(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatusEnum.PENDING) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_INVALID_STATUS);
        }
    }

    private void requireRespondingParty(Reservation reservation) {
        UserAccount currentUser = permissionCheckService.getCurrentUser();
        if (MEMBER.equals(reservation.getInitiatorType())) {
            Long currentManicuristId = manicuristRepository.findByUserId(currentUser.getId())
                    .map(Manicurist::getId)
                    .orElse(null);
            if (!reservation.getManicuristId().equals(currentManicuristId)) {
                throw new BusinessException(ErrorCodeEnum.RESERVATION_NOT_PARTICIPANT);
            }
        } else if (MANICURIST.equals(reservation.getInitiatorType())) {
            if (!reservation.getUserId().equals(currentUser.getId())) {
                throw new BusinessException(ErrorCodeEnum.RESERVATION_NOT_PARTICIPANT);
            }
        } else {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_INVALID_STATUS);
        }
    }

    private void requireParticipant(Reservation reservation) {
        UserAccount user = permissionCheckService.getCurrentUser();
        boolean isMember = reservation.getUserId().equals(user.getId());
        boolean isManicurist = manicuristRepository.findByUserId(user.getId())
                .map(Manicurist::getId)
                .filter(reservation.getManicuristId()::equals)
                .isPresent();
        if (!isMember && !isManicurist) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_NOT_PARTICIPANT);
        }
    }

    private void requireCurrentManicurist(Reservation reservation) {
        Long currentManicuristId = permissionCheckService.getCurrentManicuristId();
        if (!reservation.getManicuristId().equals(currentManicuristId)) {
            throw new BusinessException(ErrorCodeEnum.RESERVATION_NOT_PARTICIPANT);
        }
    }

    private List<ReservationServiceResponse> getServiceResponses(Long reservationId) {
        return reservationItemRepository.findByReservationIdOrderByServiceSeqAsc(reservationId)
                .stream()
                .map(this::toServiceResponse)
                .toList();
    }

    private List<ReservationItem> createReservationItems(
            Long reservationId,
            List<ServiceItem> serviceItems,
            LocalDateTime now) {
        List<ReservationItem> details = new ArrayList<>(serviceItems.size());
        long serviceSeq = 1;
        for (ServiceItem item : serviceItems) {
            ReservationItem detail = new ReservationItem();
            detail.setReservationId(reservationId);
            detail.setServiceSeq(serviceSeq++);
            detail.setServiceId(item.getId());
            detail.setServiceName(item.getServiceName());
            detail.setPrice(item.getPrice());
            detail.setDurationMin(item.getDurationMin());
            detail.setCreateTime(now);
            details.add(detail);
        }
        return details;
    }

    private ReservationServiceResponse toServiceResponse(ReservationItem item) {
        return new ReservationServiceResponse(
                item.getServiceId(),
                item.getServiceName(),
                item.getPrice(),
                item.getDurationMin());
    }

    private ReservationResponse toResponse(
            Reservation reservation,
            List<ReservationServiceResponse> services) {
        LocalDateTime lastActionTime = reservation.getUpdateTime() != null
                ? reservation.getUpdateTime()
                : reservation.getCreateTime();
        LocalDateTime deadline = reservation.getStatus() == ReservationStatusEnum.PENDING
                && lastActionTime != null
                        ? lastActionTime.plusHours(RESPONSE_WINDOW_HOURS)
                        : null;
        return new ReservationResponse(
                reservation.getId(),
                reservation.getCode(),
                reservation.getShopId(),
                reservation.getUserId(),
                reservation.getManicuristId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus(),
                reservation.getInitiatorType(),
                reservation.getTotalDurationMin(),
                reservation.getTotalPrice(),
                reservation.getCustomerNote(),
                reservation.getManicuristNote(),
                deadline,
                services);
    }

    private record ServiceTotals(List<ServiceItem> items, BigDecimal price, int durationMin) {
    }

    private record MinuteRange(int startMinute, int endMinute) {
    }
}
