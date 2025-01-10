package com.calendarapp.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.calendarapp.model.Reservation;
import com.calendarapp.model.Table;
import com.calendarapp.model.User;
import com.calendarapp.repository.ReservationRepository;
import com.calendarapp.repository.TableRepository;
import com.calendarapp.validator.ReservationValidator;
import com.calendarapp.validator.UserValidator;
import com.calendarapp.rest.DailyReservations;
import com.calendarapp.rest.ReservationRequest;
import com.calendarapp.rest.ReservationSlot;
import com.calendarapp.rest.TableReservationSlots;


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
                for (int minute = 0; minute < 60; minute += SLOT_SIZE_IN_MINUTES) {
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


    public void createReservation(ReservationRequest request) {   
        Table table = reservationValidator.validateAndGetTable(request.getTableId());
        reservationValidator.validateSlotAvailability(request, table);
        reservationValidator.validateSlot(request.getSlotStartTimes());
        User currentUser = userValidator.validateAndGetUser(request.getUsername());
    
        for (LocalTime startTime : request.getSlotStartTimes()) {
            Reservation reservation = new Reservation();
            reservation.setUser(currentUser);
            reservation.setTable(table);
            reservation.setDate(request.getDate());
            reservation.setSlotStartTime(startTime);
            reservationRepository.save(reservation);
        }
    }
}
