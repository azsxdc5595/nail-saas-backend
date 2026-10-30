
package com.nailsaas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.nailsaas.domain.BlacklistRequest;
import com.nailsaas.entity.ManicuristBlacklist;
import com.nailsaas.service.BlacklistService;

@RestController
@RequestMapping("/blacklist")
public class BlacklistController {


    @Autowired
    private BlacklistService service;
    
    @PostMapping("/getAll")
    public List<ManicuristBlacklist> getBlacklistByManicuristId(){
        return service.getBlacklistByCurrentManicurist();
    }

    @PostMapping("/add")
    public String addToBlacklist(@Valid @RequestBody BlacklistRequest req) {
        return service.addToBlacklist(req);
    }
    
    @DeleteMapping("/remove")
    public String removeFromBlacklist(@Valid @RequestBody BlacklistRequest req) {
        return service.removeFromBlacklist(req);
    }
}
