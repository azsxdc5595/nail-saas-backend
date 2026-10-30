package com.nailsaas.domain;

import java.util.List;

import com.nailsaas.entity.NailPost;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetNailPostReponse {

    private List<NailPost> nailPostList;

}