package com.hope_health.booking_service.repo;


import com.hope_health.booking_service.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepo extends JpaRepository<DoctorAvailability, String> {

    List<DoctorAvailability> findByDoctorIdAndDate(String doctorId, LocalDate date);

    List<DoctorAvailability> findByDoctorId(String doctorId);
}
