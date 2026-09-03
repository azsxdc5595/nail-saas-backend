
package com.nailsaas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.service.ReservationService;

@RestController
@RequestMapping("/reservation")
public class ReservationController {


    @Autowired
    private ReservationService service;

    
}
