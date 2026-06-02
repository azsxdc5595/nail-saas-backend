
package com.nailsaas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nailsaas.entity.ServiceItem;
import com.nailsaas.service.ServiceItemService;

@RestController
@RequestMapping("/api/services")
public class ServiceItemController {

    @Autowired
    private ServiceItemService service;

    @GetMapping
    public List<ServiceItem> list(){
        return service.findAll();
    }

    @PostMapping
    public ServiceItem create(@RequestBody ServiceItem item){
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}
