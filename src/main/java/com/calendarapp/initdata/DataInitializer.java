package com.calendarapp.initdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.calendarapp.model.Table;
import com.calendarapp.repository.TableRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TableRepository tableRepository;

    private static final int NUMBER_OF_TABLES = 10;

    @Autowired
    public DataInitializer(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (tableRepository.count() == 0) {
            for (int i = 1; i <= NUMBER_OF_TABLES; i++) {
                Table table = new Table();
                table.setNumber(i);
                tableRepository.save(table);
            }
            System.out.println("10 tables added to database");
        } else {
            System.out.println("Table 'restaurant table' is not empty. Omitting data initializaiton.");
        }
    }
}
