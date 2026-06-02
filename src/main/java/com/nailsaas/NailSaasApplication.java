
package com.nailsaas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Spring Boot 啟動入口
 * Spring 會從 com.nailsaas package 開始掃描所有元件
 */
@SpringBootApplication
public class NailSaasApplication {

    public static void main(String[] args) {

        SpringApplication.run(NailSaasApplication.class, args);

    }

}
