package com.calendarapp.validator;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.calendarapp.exception.ReservationException;
import com.calendarapp.model.Reservation;
import com.calendarapp.model.Table;
import com.calendarapp.repository.ReservationRepository;
import com.calendarapp.repository.TableRepository;
import com.calendarapp.rest.ReservationRequest;

@Component
public class ReservationValidator {

    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public ReservationValidator(TableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    public Table validateAndGetTable(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new ReservationException("Table not found with ID: " + tableId));
    }

    public void validateSlotAvailability(ReservationRequest request, Table table) {
        List<Reservation> existingReservations = reservationRepository.findReservationsByDateAndTable(request.getDate(), table);
        for (LocalTime startTime : request.getSlotStartTimes()) {
            if (!isSlotAvailable(existingReservations, startTime)) {
                throw new ReservationException("Slot " + startTime + " is not available for table ID: " + table.getId());
            }
        }
    }

    private boolean isSlotAvailable(List<Reservation> reservations, LocalTime startTime) {
        return reservations.stream()
                .noneMatch(reservation -> reservation.getSlotStartTime().equals(startTime));
    }
}
