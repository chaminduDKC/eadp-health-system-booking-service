package com.hope_health.booking_service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingRequest {
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private LocalDate date;
    private LocalTime time;
    private String reason;
    private String status; // e.g., "confirmed", "pending", "cancelled"
}
