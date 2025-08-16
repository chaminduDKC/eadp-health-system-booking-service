package com.hope_health.booking_service.controller;

import com.hope_health.booking_service.entity.BookingEntity;
import com.hope_health.booking_service.repo.BookingRepo;
import com.hope_health.booking_service.request.BookingRequest;
import com.hope_health.booking_service.service.AvailabilityService;
import com.hope_health.booking_service.service.BookingService;
import com.hope_health.booking_service.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingRepo bookingRepo;
    private final AvailabilityService availabilityService;

    @PostMapping("/create-booking")
    @PreAuthorize("hasRole('patient') or hasRole('admin')")
    public ResponseEntity<StandardResponse> test(@RequestBody BookingRequest request){
        System.out.println("Received booking "+ request);
        StandardResponse response = StandardResponse.builder()
                .code(200)
                .message("Booking Service is running")
                .data(bookingService.createBooking(request))
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('patient') or hasRole('admin') or hasRole('doctor')")
    @GetMapping("/find-by-patient/{patientId}")
    public ResponseEntity<StandardResponse> getBookingsByPatientId(@PathVariable String patientId) {
        StandardResponse response = StandardResponse.builder()
                .code(200)
                .message("Bookings retrieved successfully")
                .data(bookingService.getBookingsByPatientId(patientId))
                .build();
        return ResponseEntity.ok(response);
    }

    // for patient portal to get patient's own bookings

    @PreAuthorize("hasRole('patient') or hasRole('admin') or hasRole('doctor')")
    @GetMapping("/find-bookings-by-patient/{patientId}")
    public ResponseEntity<StandardResponse> getBookingsByPatient(@PathVariable String patientId) {
        StandardResponse response = StandardResponse.builder()
                .code(200)
                .message("Bookings retrieved successfully")
                .data(bookingService.getBookingsByPatient(patientId))
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('admin') or hasRole('doctor')")
    @GetMapping("/find-by-doctor/{doctorId}")
    public ResponseEntity<StandardResponse> getBookingsByDoctorId(@PathVariable String doctorId, @RequestParam int page, @RequestParam int size) {
        StandardResponse response = StandardResponse.builder()
                .code(200)
                .message("Bookings retrieved successfully")
                .data(bookingService.getBookingsByDoctorId(doctorId, page, size))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-all-bookings")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StandardResponse> findAllBookings(@RequestParam String searchText, @RequestParam int page, @RequestParam int size){
        System.out.println("HIHIHI");
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("All bookings retrieved")
                        .data(bookingService.getAllBookings(searchText, page, size))
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/count-all")
    public ResponseEntity<StandardResponse> countAllBookings() {
        long count = bookingRepo.count();
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("Total bookings count retrieved")
                        .data(count)
                        .build(),
                HttpStatus.OK
        );
    }


    @GetMapping("/count-today")
    public ResponseEntity<StandardResponse> countAllBookingsByDate(@RequestParam LocalDate date) {
        System.out.printf("Counting bookings for date: %s%n", date);
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("Total bookings count retrieved")
                        .data(bookingService.countTodayBookings(date))
                        .build(),
                HttpStatus.OK
        );
    }


    @DeleteMapping("/delete-by-booking-id/{bookingId}")
    public ResponseEntity<StandardResponse> deleteBookingById(@PathVariable String bookingId) {
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("Booking deleted successfully")
                        .data(bookingService.deleteBookingById(bookingId))
                        .build(),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/delete-booking-by-doctor/{doctorId}")
    public ResponseEntity<StandardResponse> deleteBookingByDoctorId(@PathVariable String doctorId) {
        System.out.println("Deleting bookings for doctor id: " + doctorId);
        List<BookingEntity> bookings = bookingRepo.findAllByDoctorId(doctorId);
        if (bookings.isEmpty()) {
            return new ResponseEntity<>(
                    StandardResponse.builder()
                            .code(404)
                            .message("No bookings found for this doctor")
                            .data(null)
                            .build(),
                    HttpStatus.NOT_FOUND
            );
        }
        bookings.forEach(booking -> bookingRepo.deleteById(booking.getBookingId()));
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("Bookings deleted successfully for doctor id: " + doctorId)
                        .data(null)
                        .build(),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/delete-booking-by-patient/{patientId}")
    public ResponseEntity<StandardResponse> deleteBookingByPatientId(@PathVariable String patientId) {
        List<BookingEntity> bookings = bookingRepo.findAllByPatientId(patientId);
        System.out.println("Found patients "+ bookings.toArray().length);
        if (bookings.isEmpty()) {
            return new ResponseEntity<>(
                    StandardResponse.builder()
                            .code(200)
                            .message("No bookings found for this patient")
                            .data(null)
                            .build(),
                    HttpStatus.OK
            );
        }
        bookings.forEach(booking -> bookingRepo.deleteById(booking.getBookingId()));
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("Bookings deleted successfully for patient id: " + patientId)
                        .data(null)
                        .build(),
                HttpStatus.OK
        );
    }

    @PutMapping("/update-booking-status/{bookingId}")
    @PreAuthorize("hasRole('admin') or hasRole('doctor')")
    public ResponseEntity<StandardResponse> updateBookingStatus(@PathVariable String bookingId, @RequestParam String status) {
        System.out.println("Updating booking status for bookingId: " + bookingId + " to status: " + status);
        System.out.println(status);
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("Booking status updated successfully")
                        .data(bookingService.updateBookingStatus(bookingId, status))
                        .build(),
                HttpStatus.OK
        );
    }

    @PutMapping("/update-payment-status/{bookingId}")
    @PreAuthorize("hasRole('admin') or hasRole('doctor')")
    public ResponseEntity<StandardResponse> updateBookingPaymentStatus(@PathVariable String bookingId, @RequestParam String paymentStatus) {
        System.out.println("Updating booking status for bookingId: " + bookingId + " to status: " + paymentStatus);
        System.out.println(paymentStatus);
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("Booking status updated successfully")
                        .data(bookingService.updateBookingPaymentStatus(bookingId, paymentStatus))
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/available-slots/{doctorId}")
    public ResponseEntity<StandardResponse> allSlots(@PathVariable String doctorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date ){
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("Available slots retrieved")
                        .data(bookingService.getAvailableSlots(doctorId, date))
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-available-dates-by-doctor/{doctorId}")
    public ResponseEntity<StandardResponse> getAvailableDatesByDoctor(@PathVariable String doctorId){
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("availabe dates for doctor")
                        .data(availabilityService.availableDatesForDoctor(doctorId))
                        .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-bookings-by-date")
    public ResponseEntity<StandardResponse> getBookingsByDate(){
        return new ResponseEntity<>(
                StandardResponse.builder()
                        .code(200)
                        .message("bookings by date for analytics")
                        .data(bookingService.getBookingsByDate())
                        .build(),
                HttpStatus.OK
        );
    }


}
