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
public class DoctorAvailabilityRequest {

    private String doctorId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
