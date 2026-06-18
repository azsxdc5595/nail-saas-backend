
package com.nailsaas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.domain.BlacklistRequest;
import com.nailsaas.entity.ManicuristBlacklist;
import com.nailsaas.service.BlacklistService;

@RestController
@RequestMapping("/blacklist")
public class BlacklistController {


    @Autowired
    private BlacklistService service;
    
    @PostMapping("/getAll")
    public List<ManicuristBlacklist> getBlacklistByManicuristId(@RequestBody BlacklistRequest req){
        return service.getBlacklistByManicuristId(req);
    }

    @PostMapping("/add")
    public String addToBlacklist(@RequestBody BlacklistRequest req) {
        return service.addToBlacklist(req);
    }
    
    @PostMapping("/remove")
    public String removeFromBlacklist(@RequestBody BlacklistRequest req) {
        return service.removeFromBlacklist(req);
    }
}
