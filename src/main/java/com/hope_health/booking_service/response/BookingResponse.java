package com.hope_health.booking_service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {
    private String bookingId;
    private String patientId;
    private String doctorId;
    private String patientName;
    private String doctorName;
    private String date; // Use String for simplicity, can be LocalDate if needed
    private String time; // Use String for simplicity, can be LocalTime if needed
    private String reason;
    private String status; // e.g., "confirmed", "pending", "cancelled"
    private String paymentStatus;
}
