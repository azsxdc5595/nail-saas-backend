package com.nailsaas.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "MANICURIST_BLOCK_TIME", schema = "NSAS")
@Data
public class ManicuristBlockTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOCK_ID")
    private Long id;

    @Column(name = "MANICURIST_ID")
    private Long manicuristId;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;
    
    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Column(name = "BLOCK_TYPE")
    private String blockType;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;
}