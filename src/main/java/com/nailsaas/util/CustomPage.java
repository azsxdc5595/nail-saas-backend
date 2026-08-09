package com.nailsaas.util;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 自訂分頁條件。
 *
 * <p>
 * 統一管理系統中所有需要分頁的查詢條件。
 * </p>
 *
 * <p>
 * 頁碼從 1 開始，每頁預設 20 筆，最多 100 筆。
 * </p>
 */
@Data
public class CustomPage {

    private Integer pageNo = 1;

    private Integer pageSize = 20;

    private static final int MAX_PAGE_SIZE = 100;

    public int getPageNoValue() {

        if (pageNo == null || pageNo < 1) {
            return 1;
        }

        return pageNo;
    }

    public int getPageSizeValue() {

        if (pageSize == null || pageSize < 1) {
            return 20;
        }

        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    public Pageable toPageable() {

        return PageRequest.of(
                getPageNoValue() - 1,
                getPageSizeValue()
        );
    }
}