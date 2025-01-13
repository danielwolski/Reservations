package com.calendarapp.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.calendarapp.model.Reservation;
import com.calendarapp.model.Table;
import com.calendarapp.model.User;
import com.calendarapp.repository.ReservationRepository;
import com.calendarapp.repository.TableRepository;
import com.calendarapp.validator.ReservationValidator;
import com.calendarapp.validator.UserValidator;

import lombok.extern.slf4j.Slf4j;

import com.calendarapp.rest.byday.RestDailyReservations;
import com.calendarapp.rest.byday.RestReservationSlot;
import com.calendarapp.rest.byday.RestTableReservationSlots;
import com.calendarapp.rest.byuser.RestReservation;
import com.calendarapp.rest.create.RestReservationRequest;

@Slf4j
@Service
public class ReservationService {
    public final static int SLOT_SIZE_IN_MINUTES = 30;
    
    private final static int DAY_START = 12;
    private final static int DAY_END = 22;

    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;

    private final ReservationValidator reservationValidator;
    private final UserValidator userValidator;

    public ReservationService(ReservationRepository reservationRepository,
                              TableRepository tableRepository,
                              ReservationValidator reservationValidator,
                              UserValidator userValidator) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
        this.reservationValidator = reservationValidator;
        this.userValidator = userValidator;
    }

    public RestDailyReservations getSlots(LocalDate date) {
        log.info("Fetching reservation slots for date: {}", date);
        List<Table> tables = tableRepository.findAll();
        RestDailyReservations dailyReservations = new RestDailyReservations();
        dailyReservations.setDate(date.toString());

        List<RestTableReservationSlots> tableReservationSlotsList = new ArrayList<>();

        List<Reservation> reservations = reservationRepository.findByDate(date);

        for (Table table : tables) {
            RestTableReservationSlots tableReservationSlots = new RestTableReservationSlots();
            tableReservationSlots.setTableId(table.getId().intValue());

            List<RestReservationSlot> reservationSlots = new ArrayList<>();

            for (int hour = DAY_START; hour < DAY_END; hour++) {
                for (int minute = 0; minute < 60; minute += SLOT_SIZE_IN_MINUTES) {
                    LocalTime slotStartTime = LocalTime.of(hour, minute);
                    
                    boolean isAvailable = true;

                    for (Reservation reservation : reservations) {
                        if (reservation.getTable().equals(table) && reservation.getSlotStartTime().equals(slotStartTime)) {
                            isAvailable = false;
                            break;
                        }
                    }

                    RestReservationSlot reservationSlot = new RestReservationSlot();
                    reservationSlot.setStartTime(slotStartTime.toString());
                    reservationSlot.setAvailable(isAvailable);

                    reservationSlots.add(reservationSlot);
                }
            }

            tableReservationSlots.setReservationSlots(reservationSlots);
            tableReservationSlotsList.add(tableReservationSlots);
        }
        dailyReservations.setTableReservationSlots(tableReservationSlotsList);
        log.info("Successfully fetched reservation slots for date: {}", date);
        return dailyReservations;
    }


    public void createReservation(RestReservationRequest request) {
        log.info("Creating reservation for user: {} on date: {}", request.getUsername(), request.getDate());
        Table table = reservationValidator.validate(request);
        User currentUser = userValidator.validateAndGetUser(request.getUsername());
    
        for (LocalTime startTime : request.getSlotStartTimes()) {
            Reservation reservation = new Reservation();
            reservation.setUser(currentUser);
            reservation.setTable(table);
            reservation.setDate(request.getDate());
            reservation.setSlotStartTime(startTime);
            reservationRepository.save(reservation);
            log.info("Reservation created for user: {}, table ID: {}, start time: {}", request.getUsername(), table.getId(), startTime);
        }
    }

    public void cancelReservation(List<Long> slotIds) {
        log.info("Cancelling reservations with IDs: {}", slotIds);
        reservationRepository.deleteAllById(slotIds);
        log.info("Successfully cancelled reservations with IDs: {}", slotIds);
    }

    public List<RestReservation> getSlotsByUser(String username) {
        log.info("Fetching reservation slots for user: {}", username);
        List<Reservation> userReservations = reservationRepository.findByUser_Username(username);
    
        userReservations.sort(Comparator.comparing(Reservation::getDate)
                                         .thenComparing(Reservation::getSlotStartTime));
    
        List<RestReservation> restReservations = new ArrayList<>();
        Reservation sequenceStart = null;
        Reservation current = null;
        List<Long> currentReservationIds = new ArrayList<>();
    
        for (Reservation reservation : userReservations) {
            if (current == null) {
                sequenceStart = reservation;
                current = reservation;
                currentReservationIds.add(reservation.getId());
            } else {
                if (current.getDate().equals(reservation.getDate()) &&
                    current.getSlotStartTime().plusMinutes(SLOT_SIZE_IN_MINUTES).equals(reservation.getSlotStartTime())) {
                    current = reservation;
                    currentReservationIds.add(reservation.getId());
                } else {
                    restReservations.add(mapToRestReservation(sequenceStart, current, currentReservationIds));
                    sequenceStart = reservation;
                    current = reservation;
                    currentReservationIds = new ArrayList<>();
                    currentReservationIds.add(reservation.getId());
                }
            }
        }
    
        if (sequenceStart != null && current != null) {
            restReservations.add(mapToRestReservation(sequenceStart, current, currentReservationIds));
        }
    
        log.info("Successfully fetched reservation slots for user: {}", username);
        return restReservations;
    }
    
    private RestReservation mapToRestReservation(Reservation start, Reservation end, List<Long> reservationIds) {
        RestReservation restReservation = new RestReservation();
        restReservation.setDate(start.getDate().toString());
        restReservation.setStartTime(start.getSlotStartTime().toString());
        restReservation.setEndTime(end.getSlotStartTime().plusMinutes(SLOT_SIZE_IN_MINUTES).toString());
        restReservation.setTableId(start.getTable().getId().intValue());
        restReservation.setSlotsIds(reservationIds);
        return restReservation;
    }
}
