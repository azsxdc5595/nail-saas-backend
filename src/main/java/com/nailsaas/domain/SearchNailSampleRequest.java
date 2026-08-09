package com.nailsaas.domain;

import java.math.BigDecimal;

import com.nailsaas.util.CustomPage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchNailSampleRequest extends CustomPage {

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
}