
package com.nailsaas.service;

import org.springframework.stereotype.Service;

import com.nailsaas.repository.ManicuristRepository;
import com.nailsaas.repository.NailPostRepository;

@Service
public class ReservationService {
    
    private final ManicuristRepository manicuristRepository;
    
    private final NailPostRepository NailPostRepository;

    ReservationService(ManicuristRepository manicuristRepository, NailPostRepository NailPostRepository) {
        this.manicuristRepository = manicuristRepository;
        this.NailPostRepository = NailPostRepository;
    }

   
}
