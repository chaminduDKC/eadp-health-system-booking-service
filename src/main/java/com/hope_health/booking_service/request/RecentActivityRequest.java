package com.hope_health.booking_service.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentActivityRequest {

    private String action;
    private String description;
    private LocalDateTime dateTime;
}
