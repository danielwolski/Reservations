package com.calendarapp.rest.byuser;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestReservation {
    private List<Long> slotsIds;
    private String date;
    private String startTime;
    private String endTime;
    private int tableId;
}
