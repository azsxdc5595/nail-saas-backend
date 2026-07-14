
package com.nailsaas.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nailsaas.domain.GetManicuristInfoReponse;
import com.nailsaas.domain.GetManicuristInfoRequest;
import com.nailsaas.domain.UpdateManicuristRequest;
import com.nailsaas.entity.Manicurist;
import com.nailsaas.repository.ManicuristRepository;

@Service
public class ManicuristService {

    @Autowired
    private ManicuristRepository manicuristRepository;
    
    public void updateMe(UpdateManicuristRequest req) {

        Optional<Manicurist> optionalManicurist = Optional.ofNullable(manicuristRepository.findByCode(req.getManicuristCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到美甲師資料")));
        Manicurist manicurist = optionalManicurist.get();

        // 只更新有傳的欄位
        if (req.getDisplayName() != null) {
            manicurist.setDisplayName(req.getDisplayName());
        }

        if (req.getIntro() != null) {
            manicurist.setIntro(req.getIntro());
        }

        manicurist.setUpdateTime(LocalDateTime.now());

        manicuristRepository.save(manicurist);
    }

    // 查詢
    public GetManicuristInfoReponse getManicuristInfo(GetManicuristInfoRequest req) {
        Optional<Manicurist> optionalManicurist = Optional.ofNullable(manicuristRepository.findByCode(req.getManicuristCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到美甲師資料")));
        Manicurist manicurist = optionalManicurist.get();
        GetManicuristInfoReponse reponse = GetManicuristInfoReponse.builder().displayName(manicurist.getDisplayName()).intro(manicurist.getIntro()).status(manicurist.getStatus()).build();
        return reponse;
    }

}