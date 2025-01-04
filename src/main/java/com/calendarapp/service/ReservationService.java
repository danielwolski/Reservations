package com.calendarapp.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.calendarapp.model.Reservation;
import com.calendarapp.model.Table;
import com.calendarapp.model.User;
import com.calendarapp.repository.ReservationRepository;
import com.calendarapp.repository.TableRepository;
import com.calendarapp.rest.ReservationRequest;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              TableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
    }

    public Map<Integer, List<String>> getAvailableSlots(LocalDate date) {
        List<Table> tables = tableRepository.findAll();
        Map<Integer, List<String>> availability = new HashMap<>();

        for (Table table : tables) {
            List<Reservation> reservations = reservationRepository.findReservationsByDateAndTable(date, table);
            List<String> slots = generateAvailableSlots(reservations);
            availability.put(table.getNumber(), slots);
        }
        return availability;
    }

    public String createReservation(ReservationRequest request, User currentUser) {
        Table table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));

        List<Reservation> existingReservations = reservationRepository.findReservationsByDateAndTable(request.getDate(), table);
        if (!isSlotAvailable(existingReservations, request.getStartTime(), request.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot is not available");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(currentUser);
        reservation.setTable(table);
        reservation.setDate(request.getDate());
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservationRepository.save(reservation);

        return "Reservation created successfully";
    }

    private List<String> generateAvailableSlots(List<Reservation> reservations) {
        List<String> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(12, 0);
        LocalTime end = LocalTime.of(22, 0);

        while (start.isBefore(end)) {
            LocalTime slotEnd = start.plusMinutes(30);

            final LocalTime slotStart = start;

            boolean isAvailable = reservations.stream()
                .noneMatch(reservation -> 
                    !reservation.getEndTime().isBefore(slotStart) && 
                    !reservation.getStartTime().isAfter(slotEnd)
                );

            slots.add(slotStart + "-" + slotEnd + (isAvailable ? " Available" : " Reserved"));
            start = slotEnd;
        }

        return slots;
    }

    private boolean isSlotAvailable(List<Reservation> reservations, LocalTime startTime, LocalTime endTime) {
        return reservations.stream()
                .noneMatch(reservation -> !reservation.getEndTime().isBefore(startTime) && !reservation.getStartTime().isAfter(endTime));
    }
}
