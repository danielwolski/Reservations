package com.calendarapp.validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.calendarapp.exception.ReservationException;
import com.calendarapp.model.Reservation;
import com.calendarapp.model.Table;
import com.calendarapp.repository.ReservationRepository;
import com.calendarapp.repository.TableRepository;
import com.calendarapp.rest.create.RestReservationRequest;

import static com.calendarapp.service.ReservationService.SLOT_SIZE_IN_MINUTES;

@Component
public class ReservationValidator {

    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public ReservationValidator(TableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    public Table validate(RestReservationRequest request) {
        Table table = validateAndGetTable(request.getTableId());
        validateUserReservations(request.getUsername(), request.getDate());
        validateSlotAvailability(request, table);
        validateSlot(request.getSlotStartTimes());
        return table;
    }

    private Table validateAndGetTable(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new ReservationException("Table not found with ID: " + tableId));
    }

    private void validateUserReservations(String username, LocalDate date) {
        boolean userHasReservation = reservationRepository.existsByUser_UsernameAndDate(username, date);
        if (userHasReservation) {
            throw new ReservationException("User " + username + " already has a reservation on " + date);
        }
    }

    private void validateSlotAvailability(RestReservationRequest request, Table table) {
        List<Reservation> existingReservations = reservationRepository.findReservationsByDateAndTable(request.getDate(), table);
        for (LocalTime startTime : request.getSlotStartTimes()) {
            if (!isSlotAvailable(existingReservations, startTime)) {
                throw new ReservationException("Slot " + startTime + " is not available for table " + table.getId());
            }
        }
    }

    private void validateSlot(List<LocalTime> slotStartTimes) {
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
