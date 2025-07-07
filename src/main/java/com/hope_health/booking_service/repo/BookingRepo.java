package com.hope_health.booking_service.repo;

import com.hope_health.booking_service.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepo extends JpaRepository<BookingEntity, String> {

    @Query(value = "SELECT COUNT(booking_id) FROM bookings WHERE patient_id = %?1%", nativeQuery = true)
    long countByPatientId(String patientId);

    @Query(value = "SELECT * FROM bookings WHERE patient_id = %?1%", nativeQuery = true)
    List<BookingEntity> findAllByPatientId(String patientId);

    Optional<BookingEntity> findByPatientId(String patientId);

    Optional<BookingEntity> findByDoctorId(String doctorId);

    boolean existsByDoctorIdAndDateAndTime(String doctorId, LocalDate date, LocalTime time);

    List<BookingEntity> findByDoctorIdAndDate(String doctorId, LocalDate date);

    Set<BookingEntity> findAllByDoctorId(String doctorId);
}
