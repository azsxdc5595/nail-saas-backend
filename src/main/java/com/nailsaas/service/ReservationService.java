
package com.nailsaas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.NailPostRepository;

@Service
public class ReservationService {
    
    @Autowired
    private ManicuristRepository manicuristRepository;
    
    @Autowired
    private NailPostRepository NailPostRepository;

   
}
