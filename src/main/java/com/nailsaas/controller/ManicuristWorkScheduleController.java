package com.nailsaas.controller;

import java.time.LocalDate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.ManicuristWorkScheduleResponse;
import com.nailsaas.domain.ReplaceManicuristWorkScheduleRequest;
import com.nailsaas.service.ManicuristWorkScheduleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/manicuristWorkSchedule")
@PreAuthorize("hasAnyRole('MANICURIST', 'SHOP_MANAGER', 'ADMIN')")
public class ManicuristWorkScheduleController {

    private final ManicuristWorkScheduleService service;

    public ManicuristWorkScheduleController(ManicuristWorkScheduleService service) {
        this.service = service;
    }

    @GetMapping("/{workDate}")
    public ManicuristWorkScheduleResponse getSchedule(@PathVariable LocalDate workDate) {
        return service.getSchedule(workDate);
    }

    @PutMapping("/{workDate}")
    public ManicuristWorkScheduleResponse replaceSchedule(
            @PathVariable LocalDate workDate,
            @Valid @RequestBody ReplaceManicuristWorkScheduleRequest request) {
        return service.replaceSchedule(workDate, request);
    }
}
