package com.nailsaas.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class GetNailSampleDetailResponse {

    /** 作品ID */
    private Long sampleId;

    /** 美甲師ID */
    private Long manicuristId;

    /** 作品圖片 */
    private String imageUrl;

    /** 價格 */
    private BigDecimal price;

    /** 作品描述 */
    private String description;

    /** 作品風格代碼 */
    private String styleCode;

    /** 季節代碼 */
    private String seasonCode;

    /** 主色系代碼 */
    private String mainColorCode;

    /** 是否可預約 */
    private Boolean reservable;

}