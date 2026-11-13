package com.nailsaas.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.CreateManicuristReservationRequest;
import com.nailsaas.domain.CreateMemberReservationRequest;
import com.nailsaas.domain.ProposeReservationServicesRequest;
import com.nailsaas.domain.ProposeReservationTimeRequest;
import com.nailsaas.domain.ReservationResponse;
import com.nailsaas.domain.ReservationAvailabilityResponse;
import com.nailsaas.service.ReservationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/reservations")
@Validated
public class ReservationController {

    private static final int MAX_PAGE_SIZE = 100;

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping("/member")
    @PreAuthorize("hasRole('MEMBER')")
    public ReservationResponse createByMember(@Valid @RequestBody CreateMemberReservationRequest request) {
        return service.createByMember(request);
    }

    @PostMapping("/manicurist")
    @PreAuthorize("hasRole('MANICURIST')")
    public ReservationResponse createByManicurist(@Valid @RequestBody CreateManicuristReservationRequest request) {
        return service.createByManicurist(request);
    }

    @GetMapping("/availability")
    @PreAuthorize("hasAnyRole('MEMBER', 'MANICURIST')")
    public ReservationAvailabilityResponse getAvailability(
            @RequestParam("manicuristId") @Positive Long manicuristId,
            @RequestParam("workDate") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            @RequestParam(value = "serviceIds", required = false) @Size(max = 20)
                    List<@NotNull @Positive Long> serviceIds,
            @RequestParam(value = "reservationId", required = false) @Positive Long reservationId) {
        return service.getAvailability(manicuristId, workDate, serviceIds, reservationId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'MANICURIST')")
    public Page<ReservationResponse> getMine(@PageableDefault(size = 20) Pageable pageable) {
        Pageable boundedPageable = PageRequest.of(
                pageable.getPageNumber(), Math.min(pageable.getPageSize(), MAX_PAGE_SIZE));
        return service.getMine(boundedPageable);
    }

    @GetMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'MANICURIST')")
    public ReservationResponse getById(@PathVariable Long reservationId) {
        return service.getById(reservationId);
    }

    @PostMapping("/{reservationId}/confirm")
    @PreAuthorize("hasAnyRole('MEMBER', 'MANICURIST')")
    public ReservationResponse confirm(
            @PathVariable Long reservationId) {
        return service.confirm(reservationId);
    }

    @PostMapping("/{reservationId}/reject")
    @PreAuthorize("hasAnyRole('MEMBER', 'MANICURIST')")
    public ReservationResponse reject(@PathVariable Long reservationId) {
        return service.reject(reservationId);
    }

    @PostMapping("/{reservationId}/propose-time")
    @PreAuthorize("hasRole('MANICURIST')")
    public ReservationResponse proposeTime(
            @PathVariable Long reservationId,
            @Valid @RequestBody ProposeReservationTimeRequest request) {
        return service.proposeTime(reservationId, request);
    }

    @PostMapping("/{reservationId}/propose-services")
    @PreAuthorize("hasRole('MANICURIST')")
    public ReservationResponse proposeServices(
            @PathVariable Long reservationId,
            @Valid @RequestBody ProposeReservationServicesRequest request) {
        return service.proposeServices(reservationId, request);
    }

    @PostMapping("/{reservationId}/cancel")
    @PreAuthorize("hasAnyRole('MEMBER', 'MANICURIST')")
    public ReservationResponse cancel(@PathVariable Long reservationId) {
        return service.cancel(reservationId);
    }

    @PostMapping("/{reservationId}/finish")
    @PreAuthorize("hasRole('MANICURIST')")
    public ReservationResponse finish(@PathVariable Long reservationId) {
        return service.finish(reservationId);
    }

    @PostMapping("/{reservationId}/no-show")
    @PreAuthorize("hasRole('MANICURIST')")
    public ReservationResponse markNoShow(@PathVariable Long reservationId) {
        return service.markNoShow(reservationId);
    }
}
