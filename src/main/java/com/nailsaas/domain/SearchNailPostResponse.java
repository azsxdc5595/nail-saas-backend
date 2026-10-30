package com.nailsaas.domain;

import java.util.List;

import lombok.Data;

@Data
public class SearchNailPostResponse {

    /** 搜尋結果 */
    private List<SearchNailPostData> nailPostList;

    /** 總筆數 */
    private Integer totalCount;

}