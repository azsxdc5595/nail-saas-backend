
package com.nailsaas.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.service.ReservationService;

@RestController
@RequestMapping("/reservation")
public class ReservationController {


    private final ReservationService service;

    ReservationController(ReservationService service) {
        this.service = service;
    }

    
}
