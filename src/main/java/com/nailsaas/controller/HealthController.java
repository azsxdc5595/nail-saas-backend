
package com.nailsaas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 系統健康檢查 API
 * 用來確認後端服務是否正常啟動
 */
@RestController
public class HealthController {

    /*
     * GET /api/health
     */
    @GetMapping("/api/health")
    public String health(){

        return "Nail SaaS Backend Running";

    }

}
