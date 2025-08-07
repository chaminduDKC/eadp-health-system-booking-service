package com.hope_health.booking_service.service.impl;

import com.hope_health.booking_service.config.WebClientConfig;
import com.hope_health.booking_service.request.RecentActivityRequest;
import com.hope_health.booking_service.response.BookingResponse;
import com.hope_health.booking_service.entity.BookingEntity;
import com.hope_health.booking_service.request.BookingRequest;
import com.hope_health.booking_service.service.BookingService;
import com.hope_health.booking_service.repo.BookingRepo;
import com.hope_health.booking_service.util.BookingsResponsePaginated;
import com.hope_health.booking_service.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final WebClientConfig webClientConfig;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        if(bookingRepo.existsByDoctorIdAndDateAndTime(
                request.getDoctorId(), request.getDate(), request.getTime())) {
            throw new RuntimeException("Booking already exists for this doctor on the given date and time");
        }
        // converting
        BookingEntity bookingEntity = toEntity(request);

        // save booking entity to the db

        try {

            bookingRepo.save(bookingEntity);

            RecentActivityRequest activityRequest = RecentActivityRequest.builder()
                    .action("Appointment created by admin")
                    .dateTime(LocalDateTime.now())
                    .description("Doctor name : " + request.getDoctorName() + ", Patient name : "+ request.getPatientName())
                    .build();

            webClientConfig.webClient().post().uri("http://localhost:9094/api/recent-activities/create-activity")
                    .bodyValue(activityRequest)
                    .retrieve()
                    .bodyToMono(StandardResponse.class)
                    .block();
            return toResponse(bookingEntity);

        } catch (WebClientException e){
            return toResponse(bookingEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


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
    public BookingsResponsePaginated getBookingsByDoctorId(String doctorId, int page, int size) {
        Optional<List<BookingEntity>> isExists = bookingRepo.findByDoctorId(doctorId);

        if(isExists.isEmpty()){
            throw new RuntimeException("No bookings for this doctor id "+ doctorId);
        }
        return BookingsResponsePaginated.builder()
                .bookingCount(bookingRepo.countByDoctorId(doctorId))
                .bookingList(bookingRepo.findAllPaginatedByDoctorId(doctorId, PageRequest.of(page, size)).stream().map(this::toResponse).toList())
                .build();
    }

    @Override
    public BookingsResponsePaginated getAllBookings(String searchText, int page, int size) {
        System.out.println("request came with search " + searchText + "page " + page + "size " + size);
        return BookingsResponsePaginated.builder()
                .bookingCount(bookingRepo.countAll(searchText))
                .bookingList(bookingRepo.searchAll(searchText,PageRequest.of(page, size)).stream()
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
            BookingEntity entity = bookingRepo.findById(bookingId).orElseThrow(()-> new RuntimeException("No user"));
            String docName = entity.getDoctorName();
            String patName = entity.getPatientName();
            LocalDate date = entity.getDate();
            LocalTime time = entity.getTime();
            bookingRepo.deleteById(bookingId);

            RecentActivityRequest activityRequest = RecentActivityRequest.builder()
                    .action("Appointment deleted by admin")
                    .dateTime(LocalDateTime.now())
                    .description("Doctor name : " + docName + ", Patient name : "+ patName + ", Date : "+date + ", Time : "+time)
                    .build();

            webClientConfig.webClient().post().uri("http://localhost:9094/api/recent-activities/create-activity")
                    .bodyValue(activityRequest)
                    .retrieve()
                    .bodyToMono(StandardResponse.class)
                    .block();

            return true;
        } catch (WebClientException e) {
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BookingResponse updateBookingStatus(String bookingId, String status) {
        Optional<BookingEntity> bookingEntity = bookingRepo.findById(bookingId);

        if (bookingEntity.isEmpty()) {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }
        try {

            String oldStatus = bookingEntity.get().getStatus();
            bookingEntity.get().setStatus(status);
            bookingRepo.save(bookingEntity.get());

            String docName = bookingEntity.get().getDoctorName();
            String patName = bookingEntity.get().getPatientName();

            RecentActivityRequest activityRequest = RecentActivityRequest.builder()
                    .action("Appointment status updated by admin")
                    .dateTime(LocalDateTime.now())
                    .description("Doctor name : " + docName + " Patient name : "+ patName + " New status : "+status + " Old status : "+oldStatus)
                    .build();

            webClientConfig.webClient().post().uri("http://localhost:9094/api/recent-activities/create-activity")
                    .bodyValue(activityRequest)
                    .retrieve()
                    .bodyToMono(StandardResponse.class)
                    .block();


            System.out.println(bookingEntity.get().getStatus());
            return toResponse(bookingEntity.get());
        } catch (WebClientException e){
            return toResponse(bookingEntity.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BookingResponse updateBookingPaymentStatus(String bookingId, String status) {
        Optional<BookingEntity> bookingEntity = bookingRepo.findById(bookingId);

        if (bookingEntity.isEmpty()) {
            throw new RuntimeException("Booking not found with id: " + bookingId);
        }
        try {

            String oldStatus = bookingEntity.get().getPaymentStatus();
            bookingEntity.get().setPaymentStatus(status);
            bookingRepo.save(bookingEntity.get());

            String docName = bookingEntity.get().getDoctorName();
            String patName = bookingEntity.get().getPatientName();

            RecentActivityRequest activityRequest = RecentActivityRequest.builder()
                    .action("Appointment payment status updated by admin")
                    .dateTime(LocalDateTime.now())
                    .description("Doctor name : " + docName + " Patient name : "+ patName + " New status : "+status + " Old status : "+oldStatus)
                    .build();

            webClientConfig.webClient().post().uri("http://localhost:9094/api/recent-activities/create-activity")
                    .bodyValue(activityRequest)
                    .retrieve()
                    .bodyToMono(StandardResponse.class)
                    .block();


            System.out.println(bookingEntity.get().getStatus());
            return toResponse(bookingEntity.get());
        } catch (WebClientException e){
            return toResponse(bookingEntity.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    // for patient portal
    @Override
    public List<BookingResponse> getBookingsByPatient(String patientId) {
        try {
            List<BookingEntity> bookings = bookingRepo.findAllByPatientId(patientId);

            return bookings.stream().map(this::toResponse).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
