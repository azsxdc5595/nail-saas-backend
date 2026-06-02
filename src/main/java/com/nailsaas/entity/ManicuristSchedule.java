package com.nailsaas.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "MANICURIST_SCHEDULE", schema = "NSAS")
@Data
public class ManicuristSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_ID")
    private Long id;

    @Column(name = "MANICURIST_ID")
    private Long manicuristId;

    @Column(name = "SLOT_ID")
    private Integer slotId;

    @Column(name = "SCHEDULE_DATE")
    private LocalDate scheduleDate;

    @Column(name = "AVAILABILITY")
    private Integer availability;
}