package com.nailsaas.domain;

import lombok.Data;

@Data
public class NailPostImageData {

    private Long imageId;

    private String storageName;

    private String storagePath;

    private String originalName;

    private String contentType;

    private Long fileSize;

    private Integer sortOrder;

    private Integer isCover;
}
