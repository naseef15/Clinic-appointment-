package com.clinic.repository;
import com.clinic.model.Appointment;
import org.springframework.data.jpa.repository.*;
import java.time.OffsetDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("select a from Appointment a where a.doctor.id = :doctorId and a.status = 'scheduled' and not (a.endAt <= :start or a.startAt >= :end)")
    List<Appointment> findConflicting(Long doctorId, OffsetDateTime start, OffsetDateTime end);

    List<Appointment> findByDoctorIdAndStartAtBetweenOrderByStartAt(Long doctorId, OffsetDateTime start, OffsetDateTime end);
}