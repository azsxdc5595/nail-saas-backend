package com.nailsaas.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SearchNailSampleData {

    /** 作品ID */
    private Long sampleId;

    /** 美甲師ID */
    private Long manicuristId;

    /** 圖片 */
    private String imageUrl;

    /** 價格 */
    private BigDecimal price;

    /** 描述 */
    private String description;

    /** 作品風格 */
    private String styleCode;

    /** 季節 */
    private String seasonCode;

    /** 主色系 */
    private String mainColorCode;

}