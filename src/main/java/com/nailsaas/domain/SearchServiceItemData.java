package com.nailsaas.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SearchServiceItemData {

    /** 服務項目ID */
    private Long serviceId;

    /** 美甲師ID */
    private Long manicuristId;

    /** 服務名稱 */
    private String serviceName;

    /** 價格 */
    private BigDecimal price;

    /** 項目所需時間 */
    private Integer durationMin;
    
    /** 作品描述 */
    private String description;

}