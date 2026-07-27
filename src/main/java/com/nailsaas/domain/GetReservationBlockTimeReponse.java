package com.nailsaas.domain;

import java.util.List;

import com.nailsaas.entity.ReservationBlockTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetReservationBlockTimeReponse {

    private List<ReservationBlockTime> reservationBlockTimeList;

}