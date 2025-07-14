package com.hope_health.booking_service.controller;


import com.hope_health.booking_service.request.DoctorAvailabilityRequest;
import com.hope_health.booking_service.service.AvailabilityService;
import com.hope_health.booking_service.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/test")
    public String test(){
        return "Available controller";
    }

    @PostMapping("/save-availabilities")
    public ResponseEntity<StandardResponse> saveAvailability(@RequestBody DoctorAvailabilityRequest request){
        System.out.println("request is "+request);
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(201)
                        .message("Available time saved succeeded")
                        .data(availabilityService.saveAvailability(request))
                        .build(),
                HttpStatus.CREATED
        );
    }
    @GetMapping("/get-availabilities-by-date-and-doctor/{doctorId}")
    public ResponseEntity<StandardResponse> getAvailability(@RequestParam LocalDate date, @PathVariable String doctorId){
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("Available times retrieved succeeded")
                        .data(availabilityService.getAvailabilities(date, doctorId))
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/find-selected-dates-by-doctor-id/{doctorId}")
    public ResponseEntity<StandardResponse> getSelectedDates(@PathVariable String doctorId){
        System.out.println("selected dates retrieved");
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("selected dates retrieved succeeded")
                        .data(availabilityService.getSelectedDates(doctorId))
                        .build(),
                HttpStatus.OK
        );
    }
}
