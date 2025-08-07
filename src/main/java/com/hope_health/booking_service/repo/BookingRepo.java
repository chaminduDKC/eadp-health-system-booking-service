package com.hope_health.booking_service.repo;

import com.hope_health.booking_service.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
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

    Optional<List<BookingEntity>> findByDoctorId(String doctorId);

    boolean existsByDoctorIdAndDateAndTime(String doctorId, LocalDate date, LocalTime time);

    List<BookingEntity> findByDoctorIdAndDate(String doctorId, LocalDate date);

    List<BookingEntity> findAllByDoctorId(String doctorId);

    long countByDate(LocalDate date);

    @Query(value = "SELECT * FROM bookings WHERE doctor_name LIKE %?1% OR patient_name LIKE %?1%", nativeQuery = true)
    Page<BookingEntity> searchAll(String search, Pageable pageable);

    @Query(value = "SELECT COUNT(booking_id) FROM bookings WHERE doctor_name LIKE %?1% OR patient_name LIKE %?1%", nativeQuery = true)
    long countAll(String search);

    @Query(nativeQuery = true, value = "SELECT COUNT(booking_id) FROM bookings WHERE doctor_id LIKE %?1%")
    long countByDoctorId(String doctorId);

    @Query(value = "SELECT * FROM bookings WHERE doctor_id LIKE %?1%", nativeQuery = true)
    Page<BookingEntity> findAllPaginatedByDoctorId(String doctorId, Pageable pageable);
}
