package com.calendarapp.rest.create;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestReservationRequest {
    private String username;
    private Long tableId;
    private LocalDate date;
    private List<LocalTime> slotStartTimes;
}
