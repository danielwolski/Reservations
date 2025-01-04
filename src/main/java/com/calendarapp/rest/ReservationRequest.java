package com.calendarapp.rest;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    private Long userId;
    private Long tableId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
