package com.calendarapp.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.calendarapp.rest.byday.RestDailyReservations;
import com.calendarapp.rest.byuser.RestReservation;
import com.calendarapp.rest.create.RestReservationRequest;
import com.calendarapp.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/{date}")
    public ResponseEntity<RestDailyReservations> getSlots(@PathVariable("date") String date) {
        LocalDate reservationDate = LocalDate.parse(date);
        RestDailyReservations availability = reservationService.getSlots(reservationDate);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<RestReservation>> getReservationsByUser(@PathVariable("username") String username) {
        List<RestReservation> userReservations = reservationService.getSlotsByUser(username);
        return ResponseEntity.ok(userReservations);
    }

    @DeleteMapping("/{slotIds}")
    public ResponseEntity<?> cancelReservation(@PathVariable("slotIds") List<Long> slotIds) {
        reservationService.cancelReservation(slotIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody RestReservationRequest request) {
        try {
            reservationService.createReservation(request);
            return ResponseEntity.ok().build(); 
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
