
package com.nailsaas.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.nailsaas.domain.ApplyReservationBlockTimeRequest;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.GetReservationBlockTimeReponse;
import com.nailsaas.domain.RemoveReservationBlockTimeRequest;
import com.nailsaas.service.ReservationBlockTimeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservationBlockTime")
public class ReservationBlockTimeController {


    private final ReservationBlockTimeService service;

    ReservationBlockTimeController(ReservationBlockTimeService service) {
        this.service = service;
    }

    @GetMapping("/getBlockTime")
    public GetReservationBlockTimeReponse getBlockTime(@RequestBody GetManicuristInfoRequest req) {
        return service.getBlockTime(req);
    }
    
    // 美甲師自己增設不可預約時間
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('MANICURIST', 'SHOP_MANAGER', 'ADMIN')")
    public void addBlockTime(@Valid @RequestBody ApplyReservationBlockTimeRequest req) {
        service.addBlockTime(req);
    }
    
    // 美甲師自己移除不可預約時間
    @DeleteMapping("/remove")
    @PreAuthorize("hasAnyRole('MANICURIST', 'SHOP_MANAGER', 'ADMIN')")
    public void removeBlockTime(@Valid @RequestBody RemoveReservationBlockTimeRequest req) {
        service.removeBlockTime(req);
    }
}
