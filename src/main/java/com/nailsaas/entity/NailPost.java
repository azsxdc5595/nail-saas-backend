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
@Table(name = "NAIL_POST", schema = "NSAS")
@Data
public class NailPost {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_post")
    @jakarta.persistence.SequenceGenerator(name = "seq_post", sequenceName = "NSAS.SEQ_NAIL_POST", allocationSize = 1)
    @Column(name = "POST_ID")
    private Long id;

    @Column(name = "MANICURIST_ID")
    private Long manicuristId;

    @Column(name = "PRICE", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "STYLE_CODE", length = 20)
    private String styleCode;

    @Column(name = "SEASON_CODE", length = 20)
    private String seasonCode;

    @Column(name = "MAIN_COLOR_CODE", length = 20)
    private String mainColorCode;

    @Column(name = "ENABLED", nullable = false, precision = 1)
    private Integer enabled;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
