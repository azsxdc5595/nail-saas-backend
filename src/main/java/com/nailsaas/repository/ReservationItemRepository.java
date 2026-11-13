package com.nailsaas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nailsaas.entity.ReservationItem;
import com.nailsaas.entity.ReservationItemId;

public interface ReservationItemRepository extends JpaRepository<ReservationItem, ReservationItemId> {

    List<ReservationItem> findByReservationIdOrderByServiceSeqAsc(Long reservationId);

    List<ReservationItem> findByReservationIdInOrderByReservationIdAscServiceSeqAsc(List<Long> reservationIds);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM ReservationItem i WHERE i.reservationId = :reservationId")
    int deleteByReservationId(@Param("reservationId") Long reservationId);
}
