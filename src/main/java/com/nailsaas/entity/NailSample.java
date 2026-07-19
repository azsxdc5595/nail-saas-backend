package com.nailsaas.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "NAIL_SAMPLE", schema = "NSAS")
@Data
public class NailSample {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sample")
    @jakarta.persistence.SequenceGenerator(name = "seq_sample", sequenceName = "NSAS.SEQ_SAMPLE", allocationSize = 1)
    @Column(name = "SAMPLE_ID")
    private Long id;

    @Column(name = "MANICURIST_ID")
    private Long manicuristId;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "STYLE_CODE")
    private String styleCode;

    @Column(name = "SEASON_CODE")
    private String seasonCode;

    @Column(name = "MAIN_COLOR_CODE")
    private String mainColorCode;

    @Column(name = "ENABLED")
    private Integer enabled;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
