package com.hope_health.booking_service.service;

import com.hope_health.booking_service.response.BookingResponse;
import com.hope_health.booking_service.request.BookingRequest;
import com.hope_health.booking_service.util.BookingsResponsePaginated;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public interface BookingService {
    BookingResponse createBooking(BookingRequest request);

    BookingsResponsePaginated getBookingsByPatientId(String patientId);

    BookingsResponsePaginated getBookingsByDoctorId(String doctorId);

    BookingsResponsePaginated getAllBookings(String searchText, int page, int size);

    boolean deleteBookingById(String bookingId);

    BookingResponse updateBookingStatus(String bookingId, String status);

    List<LocalTime> getAvailableSlots(String doctorId, LocalDate date);

    long countTodayBookings(LocalDate date);

    List<BookingResponse> getBookingsByPatient(String patientId);

    BookingResponse updateBookingPaymentStatus(String bookingId, String status);
}
