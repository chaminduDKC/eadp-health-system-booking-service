package com.hope_health.booking_service.util;

import com.hope_health.booking_service.response.BookingResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingsResponsePaginated {
    private long bookingCount;
    private List<BookingResponse> bookingList;
}
