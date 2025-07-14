package com.hope_health.booking_service.service.impl;

import com.hope_health.booking_service.response.BookingResponse;
import com.hope_health.booking_service.entity.BookingEntity;
import com.hope_health.booking_service.request.BookingRequest;
import com.hope_health.booking_service.service.BookingService;
import com.hope_health.booking_service.repo.BookingRepo;
import com.hope_health.booking_service.util.BookingsResponsePaginated;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepo bookingRepo;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        if(bookingRepo.existsByDoctorIdAndDateAndTime(
                request.getDoctorId(), request.getDate(), request.getTime())) {
            throw new RuntimeException("Booking already exists for this doctor on the given date and time");
        }
        // converting
        BookingEntity bookingEntity = toEntity(request);

        // save booking entity to the db
        bookingRepo.save(bookingEntity);
        return toResponse(bookingEntity);
    }

    @Override
    public BookingsResponsePaginated getBookingsByPatientId(String patientId) {
        Optional<BookingEntity> isExists = bookingRepo.findByPatientId(patientId);

        if(isExists.isEmpty()){
            throw new RuntimeException("No bookings for this patient id "+ patientId);
        }
        return BookingsResponsePaginated.builder()
                .bookingCount(bookingRepo.countByPatientId(patientId))
                .bookingList(bookingRepo.findAllByPatientId(patientId).stream().map(this::toResponse).toList())
                .build();
    }

    @Override
    public BookingsResponsePaginated getBookingsByDoctorId(String doctorId) {
        Optional<BookingEntity> isExists = bookingRepo.findByDoctorId(doctorId);

        if(isExists.isEmpty()){
            throw new RuntimeException("No bookings for this doctor id "+ doctorId);
        }
        return BookingsResponsePaginated.builder()
                .bookingCount(bookingRepo.countByPatientId(doctorId))
                .bookingList(bookingRepo.findAllByPatientId(doctorId).stream().map(this::toResponse).toList())
                .build();
    }

    @Override
    public BookingsResponsePaginated getAllBookings(String searchText, int page, int size) {
        return BookingsResponsePaginated.builder()
                .bookingCount(bookingRepo.count())
                .bookingList(bookingRepo.findAll(PageRequest.of(page, size)).stream()
//                        .filter(booking -> booking.getPatientId().contains(searchText) || booking.getDoctorId().contains(searchText))
                        .map(this::toResponse)
                        .toList())
                .build();
    }

    @Override
    public boolean deleteBookingById(String bookingId) {
        if (!bookingRepo.existsById(bookingId)) {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }
        try{
            bookingRepo.deleteById(bookingId);
            return true;
        } catch (Exception e) {
           return false;
        }

    }

    @Override
    public BookingResponse updateBookingStatus(String bookingId, String status) {
        Optional<BookingEntity> bookingEntity = bookingRepo.findById(bookingId);

        if (bookingEntity.isEmpty()) {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }
        System.out.println(status);
        bookingEntity.get().setStatus(status);
        bookingRepo.save(bookingEntity.get());
        System.out.println(bookingEntity.get().getStatus());
        return toResponse(bookingEntity.get());
    }

    @Override
    public List<LocalTime> getAvailableSlots(String doctorId, LocalDate date) {
        List<LocalTime> allSlots = List.of(
                LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
                LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0)
        );

        List<BookingEntity> bookings = bookingRepo.findByDoctorIdAndDate(doctorId, date);
        Set<LocalTime> bookedSlots = bookings.stream()
                .map(BookingEntity::getTime)
                .collect(Collectors.toSet());

        return allSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());

    }

    @Override
    public long countTodayBookings(LocalDate date) {
        return bookingRepo.countByDate(date);
    }

    private BookingResponse toResponse(BookingEntity entity) {
        return BookingResponse.builder()
                .bookingId(entity.getBookingId())
                .paymentStatus(entity.getPaymentStatus())
                .patientName(entity.getPatientName())
                .doctorName(entity.getDoctorName())
                .patientId(entity.getPatientId())
                .doctorId(entity.getDoctorId())
                .date(String.valueOf(entity.getDate()))
                .time(String.valueOf(entity.getTime()))
                .reason(entity.getReason())
                .status(entity.getStatus())
                .build();
    }

    private BookingEntity toEntity(BookingRequest request) {
        return BookingEntity.builder()
                .bookingId(UUID.randomUUID().toString())
                .paymentStatus(request.getPaymentStatus())
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .doctorId(request.getDoctorId())
                .doctorName(request.getDoctorName())
                .date(request.getDate())
                .time(request.getTime())
                .reason(request.getReason())
                .status(request.getStatus())
                .build();
    }
}
