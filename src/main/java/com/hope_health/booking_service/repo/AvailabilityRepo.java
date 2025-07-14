package com.hope_health.booking_service.repo;


import com.hope_health.booking_service.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepo extends JpaRepository<DoctorAvailability, String> {

    List<DoctorAvailability> findByDoctorIdAndDate(String doctorId, LocalDate date);
}
