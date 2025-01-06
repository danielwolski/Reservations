package com.calendarapp.rest;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyReservations {
    private String date;
    private List<TableReservationSlots> tableReservationSlots;
}

