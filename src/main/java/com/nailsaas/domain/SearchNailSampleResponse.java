package com.nailsaas.domain;

import java.util.List;

import lombok.Data;

@Data
public class SearchNailSampleResponse {

    /** 搜尋結果 */
    private List<SearchNailSampleData> nailSampleList;

    /** 總筆數 */
    private Integer totalCount;

}