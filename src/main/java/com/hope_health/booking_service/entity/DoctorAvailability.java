package com.hope_health.booking_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_availability")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DoctorAvailability {
    @Id
    @Column(name = "availablity_id")
    private String availabilityId;

    @Column(name = "doctor_id")
    private String doctorId;

    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;


}
