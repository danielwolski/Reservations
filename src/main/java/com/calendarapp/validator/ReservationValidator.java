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

import static  com.calendarapp.service.ReservationService.SLOT_SIZE_IN_MINUTES;

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

    public void validateSlot(List<LocalTime> slotStartTimes) {
        if (slotStartTimes.size() < 2 || slotStartTimes.size() > 4) {
            throw new ReservationException("Reserve time slot between 1 and 2 hours");
        }

        slotStartTimes.sort(LocalTime::compareTo);

        for (int i = 1; i < slotStartTimes.size(); i++) {
            LocalTime previousSlot = slotStartTimes.get(i - 1);
            LocalTime currentSlot = slotStartTimes.get(i);
            
            if (previousSlot.plusMinutes(SLOT_SIZE_IN_MINUTES).compareTo(currentSlot) != 0) {
                throw new IllegalArgumentException("Slot times must be consecutive");
            }
        }
    }

    private boolean isSlotAvailable(List<Reservation> reservations, LocalTime startTime) {
        return reservations.stream()
                .noneMatch(reservation -> reservation.getSlotStartTime().equals(startTime));
    }
}
