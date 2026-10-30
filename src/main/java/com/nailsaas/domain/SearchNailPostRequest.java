package com.nailsaas.domain;

import java.math.BigDecimal;

import com.nailsaas.util.CustomPage;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchNailPostRequest extends CustomPage {

    private String userCode;
    
    /** 美甲師對外顯示名稱 */
    private String displayName;
    
    /** 作品貼文描述 */
    private String description;

    /** 作品貼文風格代碼 */
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