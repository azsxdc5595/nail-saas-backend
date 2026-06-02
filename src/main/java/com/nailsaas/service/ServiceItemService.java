
package com.nailsaas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nailsaas.entity.ServiceItem;
import com.nailsaas.repository.ServiceItemRepository;

@Service
public class ServiceItemService {

    @Autowired
    private ServiceItemRepository repository;

    public List<ServiceItem> findAll(){
        return repository.findAll();
    }

    public ServiceItem save(ServiceItem item){
        return repository.save(item);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
}
