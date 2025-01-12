package com.calendarapp.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.calendarapp.model.Reservation;
import com.calendarapp.model.Table;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateAndTable(LocalDate date, Table table);

    List<Reservation> findByDate(LocalDate reservationDate);

    List<Reservation> findReservationsByDateAndTable(LocalDate date, Table table);

    boolean existsByUser_UsernameAndDate(String username, LocalDate date);

    List<Reservation> findByUser_Username(@Param("username") String username);
}
