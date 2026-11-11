package com.nailsaas.entity;

import java.time.LocalDate;
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
@Table(name = "MANICURIST_WORK_SCHEDULE", schema = "NSAS")
@Data
public class ManicuristWorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_manicurist_work_schedule")
    @SequenceGenerator(
            name = "seq_manicurist_work_schedule",
            sequenceName = "NSAS.SEQ_MANICURIST_WORK_SCHEDULE",
            allocationSize = 1)
    @Column(name = "SCHEDULE_ID", nullable = false)
    private Long id;

    @Column(name = "MANICURIST_ID", nullable = false)
    private Long manicuristId;

    @Column(name = "WORK_DATE", nullable = false)
    private LocalDate workDate;

    @Column(name = "START_MINUTE_OF_DAY", nullable = false)
    private Integer startMinuteOfDay;

    @Column(name = "END_MINUTE_OF_DAY", nullable = false)
    private Integer endMinuteOfDay;

    @Column(name = "CREATE_TIME", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
