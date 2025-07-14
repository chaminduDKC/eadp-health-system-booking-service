package com.hope_health.booking_service.service.impl;

import com.hope_health.booking_service.entity.BookingEntity;
import com.hope_health.booking_service.repo.BookingRepo;
import com.hope_health.booking_service.response.DoctorAvailabilityResponse;
import com.hope_health.booking_service.entity.DoctorAvailability;
import com.hope_health.booking_service.repo.AvailabilityRepo;
import com.hope_health.booking_service.request.DoctorAvailabilityRequest;
import com.hope_health.booking_service.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepo availabilityRepo;
    private final BookingRepo bookingRepo;

    @Override
    public DoctorAvailabilityResponse saveAvailability(DoctorAvailabilityRequest request) {
        try {
            DoctorAvailability saved = availabilityRepo.save(toEntity(request));
            return toResponse(saved);
        } catch (Exception e) {
            throw new RuntimeException("failed to save availabilities");
        }

    }

    @Override
    public List<LocalTime> getAvailabilities(LocalDate date, String doctorId) {
       List<DoctorAvailability> availabilities = availabilityRepo.findByDoctorIdAndDate(doctorId, date);

       List<LocalTime> timeSlots = new ArrayList<>();

       for (DoctorAvailability availability : availabilities){
           LocalTime time = availability.getStartTime();
           while (!time.isAfter(availability.getEndTime().minusMinutes(30))){
               timeSlots.add(time);
               time = time.plusMinutes(30);
           }
       }
       List<BookingEntity> booked = bookingRepo.findByDoctorIdAndDate(doctorId, date);
        Set<LocalTime> bookedTimes = booked.stream()
                .map(BookingEntity::getTime)
                .collect(Collectors.toSet());

       return timeSlots.stream()
               .filter(slot -> !bookedTimes.contains(slot))
               .collect(Collectors.toList());
    }

    @Override
    public List<LocalDate> getSelectedDates(String doctorId) {
        List<DoctorAvailability> availabilities = availabilityRepo.findByDoctorId(doctorId);
        return availabilities.stream().map(DoctorAvailability::getDate).toList();
    }


    private DoctorAvailability toEntity(DoctorAvailabilityRequest request){
        return DoctorAvailability.builder()
                .availabilityId(UUID.randomUUID().toString())
                .date(request.getDate())
                .doctorId(request.getDoctorId())
                .endTime(request.getEndTime())
                .startTime(request.getStartTime())
                .build();
    }

    private DoctorAvailabilityResponse toResponse(DoctorAvailability entity){
        return DoctorAvailabilityResponse.builder()
                .availabilityId(entity.getAvailabilityId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .doctorId(entity.getDoctorId())
                .build();
    }
}
