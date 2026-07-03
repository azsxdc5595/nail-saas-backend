package com.nailsaas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "MANICURIST_OFF_DAY", schema = "NSAS")
@Data
public class ManicuristOffDay {

    @Id
    @Column(name = "MANICURIST_ID")
    private Long manicuristId;

    @Id
    @Column(name = "DAY_OF_WEEK")
    private Integer dayOfWeek;
}