package com.nailsaas.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nailsaas.entity.Reservation;
import com.nailsaas.enums.ReservationStatusEnum;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByManicuristIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Long manicuristId,
            Collection<ReservationStatusEnum> statuses,
            LocalDateTime dayEnd,
            LocalDateTime dayStart);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.id = :reservationId")
    Optional<Reservation> findByIdForUpdate(@Param("reservationId") Long reservationId);

    Page<Reservation> findByUserIdOrManicuristIdOrderByStartTimeDesc(
            Long userId,
            Long manicuristId,
            Pageable pageable);
}
