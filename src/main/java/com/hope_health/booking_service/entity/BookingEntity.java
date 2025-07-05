package com.hope_health.booking_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingEntity {
    @Id
    @Column(name = "booking_id")
    private String bookingId;

    @Column(name = "patient_name")
    private String patientName;
    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "doctor_name")
    private String doctorName;
    @Column(name = "doctor_id")
    private String doctorId;

    private String status;
    private String reason;

    private LocalDate date;
    private LocalTime time;


}
