package com.nailsaas.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nailsaas.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByManicuristIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThanOrderByStartTimeAsc(
            Long manicuristId,
            Collection<String> statuses,
            LocalDateTime dayEnd,
            LocalDateTime dayStart);
}
