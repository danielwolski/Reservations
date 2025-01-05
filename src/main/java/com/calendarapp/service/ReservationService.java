package com.calendarapp.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.calendarapp.model.Reservation;
import com.calendarapp.model.Table;
import com.calendarapp.model.User;
import com.calendarapp.repository.ReservationRepository;
import com.calendarapp.repository.TableRepository;
import com.calendarapp.repository.UserRepository;
import com.calendarapp.rest.ReservationRequest;


@Service
public class ReservationService {
    private final static int DAY_START = 12;
    private final static int DAY_END = 12;
    private final static int SLOT_IN_MINUTES = 30;

    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              TableRepository tableRepository,
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
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

    private List<String> generateAvailableSlots(List<Reservation> reservations) {
        List<String> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(DAY_START, 0);
        LocalTime end = LocalTime.of(DAY_END, 0);
    
        Map<LocalTime, Boolean> slotAvailability = new HashMap<>();
    
        for (Reservation reservation : reservations) {
            LocalTime reservationStart = reservation.getSlotStartTime();
    
            slotAvailability.put(reservationStart, true);
        }
    
        while (start.isBefore(end)) {
            LocalTime slotEnd = start.plusMinutes(SLOT_IN_MINUTES);
            final LocalTime slotStart = start;
            boolean isAvailable = !slotAvailability.containsKey(slotStart); 
            slots.add(slotStart + "-" + slotEnd + (isAvailable ? " Available" : " Reserved"));
            start = slotEnd; 
        }

        return slots;
    }

    public String createReservation(ReservationRequest request) {
        Table table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));

        List<Reservation> existingReservations = reservationRepository.findReservationsByDateAndTable(request.getDate(), table);
        
        for (LocalTime startTime : request.getSlotStartTimes()) {
            if (!isSlotAvailable(existingReservations, startTime)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot " + startTime + " is not available");
            }
        }

        Optional<User> currentUser = userRepository.findById(request.getUserId());

        if (currentUser.isPresent()) {
            for (LocalTime startTime : request.getSlotStartTimes()) {
                Reservation reservation = new Reservation();
                reservation.setUser(currentUser.get());
                reservation.setTable(table);
                reservation.setDate(request.getDate());
                reservation.setSlotStartTime(startTime);
                reservationRepository.save(reservation);
            }
            return "Reservation created successfully";
        } else {
            return "Error getting user";
        }        
    }

    private boolean isSlotAvailable(List<Reservation> reservations, LocalTime startTime) {
        for (Reservation reservation : reservations) {
            if (reservation.getSlotStartTime().equals(startTime)) {
                return false; 
            }
        }
        return true;
    }
}
