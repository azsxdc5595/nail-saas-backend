package com.nailsaas.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SearchNailPostData {

    /** 作品貼文ID */
    private Long postId;

    /** 美甲師ID */
    private Long manicuristId;

    /** 價格 */
    private BigDecimal price;

    /** 作品貼文描述 */
    private String description;

    /** 作品貼文風格 */
    private String styleCode;

    /** 季節 */
    private String seasonCode;

    /** 主色系 */
    private String mainColorCode;

    private NailPostImageData coverImage;

}
