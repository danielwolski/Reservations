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
import com.calendarapp.rest.DailyReservations;
import com.calendarapp.rest.ReservationRequest;
import com.calendarapp.rest.ReservationSlot;
import com.calendarapp.rest.TableReservationSlots;


@Service
public class ReservationService {
    private final static int DAY_START = 12;
    private final static int DAY_END = 22;
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

    public DailyReservations getSlots(LocalDate date) {
        List<Table> tables = tableRepository.findAll();
        DailyReservations dailyReservations = new DailyReservations();
        dailyReservations.setDate(date.toString());

        List<TableReservationSlots> tableReservationSlotsList = new ArrayList<>();

        List<Reservation> reservations = reservationRepository.findByDate(date);

        for (Table table : tables) {
            TableReservationSlots tableReservationSlots = new TableReservationSlots();
            tableReservationSlots.setTableId(table.getId().intValue());

            List<ReservationSlot> reservationSlots = new ArrayList<>();

            for (int hour = DAY_START; hour < DAY_END; hour++) {
                for (int minute = 0; minute < 60; minute += SLOT_IN_MINUTES) {
                    LocalTime slotStartTime = LocalTime.of(hour, minute);
                    
                    boolean isAvailable = true;

                    for (Reservation reservation : reservations) {
                        if (reservation.getTable().equals(table) && reservation.getSlotStartTime().equals(slotStartTime)) {
                            isAvailable = false;
                            break;
                        }
                    }

                    ReservationSlot reservationSlot = new ReservationSlot();
                    reservationSlot.setStartTime(slotStartTime.toString());
                    reservationSlot.setAvailable(isAvailable);

                    reservationSlots.add(reservationSlot);
                }
            }

            tableReservationSlots.setReservationSlots(reservationSlots);
            tableReservationSlotsList.add(tableReservationSlots);
        }
        dailyReservations.setTableReservationSlots(tableReservationSlotsList);
        return dailyReservations;
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

        Optional<User> currentUser = userRepository.findByUsername(request.getUsername());

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
