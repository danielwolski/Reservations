package com.calendarapp.rest.byuser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestReservation {
    private String date;
    private String startTime;
    private String endTime;
    private int tableId;
}
