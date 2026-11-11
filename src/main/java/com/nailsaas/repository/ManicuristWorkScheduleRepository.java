package com.nailsaas.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nailsaas.entity.ManicuristWorkSchedule;

public interface ManicuristWorkScheduleRepository extends JpaRepository<ManicuristWorkSchedule, Long> {

    List<ManicuristWorkSchedule> findByManicuristIdAndWorkDateOrderByStartMinuteOfDayAsc(
            Long manicuristId,
            LocalDate workDate);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            DELETE FROM ManicuristWorkSchedule s
            WHERE s.manicuristId = :manicuristId
              AND s.workDate = :workDate
            """)
    int deleteByManicuristIdAndWorkDate(
            @Param("manicuristId") Long manicuristId,
            @Param("workDate") LocalDate workDate);
}
