package com.calendarapp.initdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.calendarapp.model.Table;
import com.calendarapp.repository.TableRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Value("${restaurant-tables.number}")
    private int numberOfTables;

    private final TableRepository tableRepository;

    @Autowired
    public DataInitializer(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (tableRepository.count() == 0) {
            for (int i = 1; i <= numberOfTables; i++) {
                Table table = new Table();
                table.setNumber(i);
                tableRepository.save(table);
            }
            log.info(numberOfTables + " tables added to database");
        } else {
            log.info("Table 'restaurant table' is not empty. Omitting data initialization.");
        }
    }
}
