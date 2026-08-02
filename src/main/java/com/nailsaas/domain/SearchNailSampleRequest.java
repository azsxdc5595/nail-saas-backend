package com.nailsaas.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SearchNailSampleRequest {

    /** 作品描述 */
    private String description;

    /** 作品風格代碼 */
    private String styleCode;

    /** 季節代碼 */
    private String seasonCode;

    /** 主色系代碼 */
    private String mainColorCode;

    /** 最低價格 */
    private BigDecimal minPrice;

    /** 最高價格 */
    private BigDecimal maxPrice;

    /** 頁碼 */
    private Integer pageNo;

    /** 每頁筆數 */
    private Integer pageSize;

}