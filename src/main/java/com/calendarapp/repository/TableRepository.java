package com.calendarapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.calendarapp.model.Table;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {
}
