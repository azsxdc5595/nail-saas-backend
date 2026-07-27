
package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.ApplyReservationBlockTimeRequest;
import com.nailsaas.domain.GetReservationBlockTimeReponse;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.RemoveReservationBlockTimeRequest;
import com.nailsaas.service.ReservationBlockTimeService;

@RestController
@RequestMapping("/reservationBlockTime")
public class ReservationBlockTimeController {


    @Autowired
    private ReservationBlockTimeService service;

    @GetMapping("/getBlockTime")
    public GetReservationBlockTimeReponse getBlockTime(@RequestBody GetManicuristInfoRequest req) {
        return service.getBlockTime(req);
    }
    
    // 美甲師自己增設不可預約時間
    @PostMapping("/add")
    public void addBlockTime(@RequestBody ApplyReservationBlockTimeRequest req) {
        service.addBlockTime(req);
    }
    
    // 美甲師自己移除不可預約時間
    @DeleteMapping("/remove")
    public void removeBlockTime(@RequestBody RemoveReservationBlockTimeRequest req) {
        service.removeBlockTime(req);
    }
}
