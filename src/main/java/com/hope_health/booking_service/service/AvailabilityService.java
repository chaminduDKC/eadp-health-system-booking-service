package com.hope_health.booking_service.service;


import com.hope_health.booking_service.response.DoctorAvailabilityResponse;
import com.hope_health.booking_service.request.DoctorAvailabilityRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface AvailabilityService {
    DoctorAvailabilityResponse saveAvailability(DoctorAvailabilityRequest request);

    List<LocalTime> getAvailabilities(LocalDate date, String doctorId);

    List<LocalDate> getSelectedDates(String doctorId);

    List<LocalDate> availableDatesForDoctor(String doctorId);
}
