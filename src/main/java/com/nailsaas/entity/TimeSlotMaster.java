package com.nailsaas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TIME_SLOT_MASTER", schema = "NSAS")
@Data
public class TimeSlotMaster {

    @Id
    @Column(name = "SLOT_ID")
    private Integer slotId;

    @Column(name = "SLOT_TIME")
    private String slotTime;
}