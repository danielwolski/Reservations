package com.calendarapp.rest.byday;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestTableReservationSlots {
    private int tableId;
    private List<RestReservationSlot> reservationSlots;
}
