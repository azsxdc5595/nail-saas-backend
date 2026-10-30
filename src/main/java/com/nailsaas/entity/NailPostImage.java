package com.nailsaas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "NAIL_POST_IMAGE", schema = "NSAS")
@Data
public class NailPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_nail_post_image")
    @SequenceGenerator(name = "seq_nail_post_image", sequenceName = "NSAS.SEQ_NAIL_POST_IMAGE", allocationSize = 1)
    @Column(name = "IMAGE_ID")
    private Long id;

    @Column(name = "POST_ID", nullable = false)
    private Long postId;

    @Column(name = "STORAGE_NAME", nullable = false, length = 255)
    private String storageName;

    @Column(name = "STORAGE_PATH", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "ORIGINAL_NAME", length = 255)
    private String originalName;

    @Column(name = "CONTENT_TYPE", nullable = false, length = 100)
    private String contentType;

    @Column(name = "FILE_SIZE", nullable = false, precision = 19)
    private Long fileSize;

    @Column(name = "SORT_ORDER", nullable = false, precision = 5)
    private Integer sortOrder;

    @Column(name = "IS_COVER", nullable = false, precision = 1)
    private Integer isCover;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
