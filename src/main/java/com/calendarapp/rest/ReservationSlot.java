package com.calendarapp.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationSlot {
    private String startTime;
    private boolean available;
}

