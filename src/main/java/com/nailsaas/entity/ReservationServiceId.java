package com.nailsaas.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationServiceId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long reservationId;
    private Long serviceSeq;
}
