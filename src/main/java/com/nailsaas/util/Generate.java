package com.nailsaas.util;

import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class Generate {

    public String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public String generateCode() {

        Random random = new Random();

        // 產生 6 位數（100000 ~ 999999）
        int code = 100000 + random.nextInt(900000);

        return String.valueOf(code);
    }
}
