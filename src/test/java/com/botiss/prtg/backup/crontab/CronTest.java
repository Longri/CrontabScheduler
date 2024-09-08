/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of CrontabScheduler.
 *
 * CrontabScheduler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * CrontabScheduler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CrontabScheduler. If not, see <https://www.gnu.org/licenses/>.
 */
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