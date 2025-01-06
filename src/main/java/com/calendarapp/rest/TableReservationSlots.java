package com.calendarapp.rest;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableReservationSlots {
    private int tableId;
    private List<ReservationSlot> reservationSlots;
}
