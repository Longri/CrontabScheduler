package com.botiss.prtg.backup.crontab;


import de.longri.crontab.Cron;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CronTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testConstructor() {

        // Every 3 minutes starting at :00 minute after the hour
        String everyThreeMinutes = "* 0/3 * ? * * *";
        Cron cron = new Cron("* 0/3 * ? * * *", "String Command");


        LocalDateTime ldt = LocalDateTime.now();
        LocalDateTime last = LocalDateTime.now();
        LocalDateTime next = LocalDateTime.now();
        int hour = ldt.getHour();
        int minute = ldt.getMinute();

        if (minute >= 3) {
            int m = minute % 3;
        } else {

        }


        ldt.plusHours(1);
    }
}