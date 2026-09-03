package com.nailsaas.domain;

import java.util.List;

import lombok.Data;

@Data
public class SearchServiceItemResponse {

    /** 搜尋結果 */
    private List<SearchServiceItemData> searchServiceItemDataList;

    /** 總筆數 */
    private Integer totalCount;

}