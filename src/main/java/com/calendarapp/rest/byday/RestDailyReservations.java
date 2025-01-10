package com.calendarapp.rest.byday;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestDailyReservations {
    private String date;
    private List<RestTableReservationSlots> tableReservationSlots;
}

