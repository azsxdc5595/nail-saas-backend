package com.nailsaas.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "REVIEW", schema = "NSAS")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long id;

    @Column(name = "RESERVATION_ID")
    private Long reservationId;

    @Column(name = "REVIEWER_ID")
    private Long reviewerId;

    @Column(name = "TARGET_ID")
    private Long targetId;

    @Column(name = "RATING")
    private Integer rating;

    @Column(name = "COMMENT_TEXT")
    private String commentText;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;
}