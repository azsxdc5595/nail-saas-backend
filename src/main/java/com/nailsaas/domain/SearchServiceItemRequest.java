package com.nailsaas.domain;

import java.math.BigDecimal;

import com.nailsaas.util.CustomPage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchServiceItemRequest extends CustomPage {

    private String userCode;
    
    /** 服務名稱 */
    private String serviceName;

    /** 最低價格 */
    private BigDecimal minPrice;

    /** 最高價格 */
    private BigDecimal maxPrice;

    /** 最低項目所需時間 */
    private Integer minDurationMin;

    /** 最高項目所需時間 */
    private Integer maxDurationMin;

    /** 作品貼文描述 */
    private String description;

}