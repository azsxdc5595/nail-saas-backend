package com.nailsaas.domain;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class GetNailPostDetailResponse {

    /** 作品貼文ID */
    private Long postId;

    /** 美甲師ID */
    private Long manicuristId;

    /** 價格 */
    private BigDecimal price;

    /** 作品貼文描述 */
    private String description;

    /** 作品貼文風格代碼 */
    private String styleCode;

    /** 季節代碼 */
    private String seasonCode;

    /** 主色系代碼 */
    private String mainColorCode;

    /** 是否可預約 */
    private Boolean reservable;

    private List<NailPostImageData> images;

}
