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

/**
 * Copyright(C)2012 Frode Carlsen
 * <p>
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import de.longri.crontab.LocalCronExpression;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class CronExpressionTest {

    @BeforeAll
    public static void setUp() {

    }

    @AfterAll
    public static void tearDown() {

    }


    @Test()
    public void shall_give_error_if_invalid_count_field() throws Exception {
        try {
            new CronExpression("* 3 *");
        } catch (ParseException e) {
            return;
        }
        Assertions.fail("Must throw IllegalArgumentException");
    }


    @Test
    public void check_all() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("* * * * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 1, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 13, 0, 2, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 2, 0, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 2, 1, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 59, 59, 0);
        expected = LocalDateTime.of(2012, 4, 10, 14, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }


    @Test
    public void check_second_number() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("3 * * * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 1, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 13, 1, 3, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 1, 3, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 2, 3, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 59, 3, 0);
        expected = LocalDateTime.of(2012, 4, 10, 14, 0, 3, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 23, 59, 3, 0);
        expected = LocalDateTime.of(2012, 4, 11, 0, 0, 3, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 30, 23, 59, 3, 0);
        expected = LocalDateTime.of(2012, 5, 1, 0, 0, 3, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_second_increment() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("5/15 * * * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 13, 0, 5, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 5, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 0, 20, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 20, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 0, 35, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 35, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 0, 50, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 50, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 1, 5, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

//        // if rolling over minute then reset second (cron rules - increment affects only values in own field)
//        after = LocalDateTime.of(2012, 4, 10, 13, 0, 50, 0);
//        expected = LocalDateTime.of(2012, 4, 10, 13, 1, 10, 0);
//        assertTrue(new LocalCronExpression("10/100 * * * * *").nextTimeAfter(after).equals(expected));
//
//        after = LocalDateTime.of(2012, 4, 10, 13, 1, 10, 0);
//        expected = LocalDateTime.of(2012, 4, 10, 13, 2, 10, 0);
//        assertTrue(new LocalCronExpression("10/100 * * * * *").nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_second_list() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("7,19 * * * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 13, 0, 7, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 7, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 0, 19, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 19, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 1, 7, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_second_range() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("42-45 * * * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 13, 0, 42, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 42, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 0, 43, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 43, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 0, 44, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 44, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 0, 45, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 0, 45, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 1, 42, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test()
    public void check_second_invalid_range() throws Exception {
        try {
            new CronExpression("42-63 * * * * *");
        } catch (ParseException e) {
            return;
        }
        Assertions.fail("Must throw IllegalArgumentException");
    }

    @Test()
    public void check_second_invalid_increment_modifier() throws Exception {
        try {
            new CronExpression("42#3 * * * * *");
        } catch (ParseException e) {
            return;
        }
        Assertions.fail("Must throw IllegalArgumentException");
    }

    @Test
    public void check_minute_number() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 3 * * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 1, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 13, 3, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 3, 0, 0);
        expected = LocalDateTime.of(2012, 4, 10, 14, 3, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_minute_increment() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0/15 * * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 13, 15, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 15, 0, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 30, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 30, 0, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 45, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 45, 0, 0);
        expected = LocalDateTime.of(2012, 4, 10, 14, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_every_minute() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0/1 * ? * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 10, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 13, 1, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.parse("2022-02-04T10:38:01.143791");
        expected = LocalDateTime.parse("2022-02-04T10:39");
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

    }

    @Test
    public void check_minute_list() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 7,19 * * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 13, 7, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 13, 7, 0, 0);
        expected = LocalDateTime.of(2012, 4, 10, 13, 19, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_hour_number() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 * 3 * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 1, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 11, 3, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 11, 3, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 11, 3, 1, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 11, 3, 59, 0, 0);
        expected = LocalDateTime.of(2012, 4, 12, 3, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_hour_increment() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 * 0/15 * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 15, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 15, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 10, 15, 1, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 15, 59, 0, 0);
        expected = LocalDateTime.of(2012, 4, 11, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 11, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 11, 0, 1, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 11, 15, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 11, 15, 1, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_hour_list() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 * 7,19 * * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 10, 19, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 19, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 10, 19, 1, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 10, 19, 59, 0, 0);
        expected = LocalDateTime.of(2012, 4, 11, 7, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_dayOfMonth_number() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 * * 3 * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 5, 3, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 5, 3, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 5, 3, 0, 1, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 5, 3, 0, 59, 0, 0);
        expected = LocalDateTime.of(2012, 5, 3, 1, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 5, 3, 23, 59, 0, 0);
        expected = LocalDateTime.of(2012, 6, 3, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_dayOfMonth_increment() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0 0 1/15 * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 16, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 16, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 5, 1, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 30, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 5, 1, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 5, 16, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_dayOfMonth_list() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0 0 7,19 * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 19, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 19, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 5, 7, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 5, 7, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 5, 19, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 5, 30, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 6, 7, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_dayOfMonth_last() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0 0 L * *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 10, 13, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 30, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 2, 12, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 2, 29, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_dayOfMonth_number_last_L() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0 0 ? * 3L *");

        //Tue Feb 22 00:00:00 UTC 2022

        LocalDateTime after = LocalDateTime.of(2022, 2, 2, 8, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2022, 2, 22, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 2, 12, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 2, 28, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_dayOfMonth_closest_weekday_W() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0 0 9W * *");

        // 9 - is weekday in may
        LocalDateTime after = LocalDateTime.of(2012, 5, 2, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 5, 9, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        // 9 - is weekday in may
        after = LocalDateTime.of(2012, 5, 8, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        // 9 - saturday, friday closest weekday in june
        after = LocalDateTime.of(2012, 5, 9, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 6, 8, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        // 9 - sunday, monday closest weekday in september
        after = LocalDateTime.of(2012, 9, 1, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 9, 10, 0, 0, 0, 0);
        LocalDateTime date = cronExpr.nextTimeAfter(after);
        assertTrue(date.equals(expected));
    }

    @Test()
    public void check_dayOfMonth_invalid_modifier() throws Exception {
        try {
            new CronExpression("0 0 0 9X * *");
        } catch (ParseException e) {
            return;
        }
        Assertions.fail("Must throw IllegalArgumentException");
    }

    @Test()
    public void check_dayOfMonth_invalid_increment_modifier() throws Exception {
        try {
            new CronExpression("0 0 0 9#2 * *");
        } catch (ParseException e) {
            return;
        }
        Assertions.fail("Must throw IllegalArgumentException");
    }

    @Test
    public void check_month_number() throws Exception {
        LocalDateTime after = LocalDateTime.of(2012, 2, 12, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 5, 1, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 1 5 *").nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_month_increment() throws Exception {
        LocalDateTime after = LocalDateTime.of(2012, 2, 12, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 5, 1, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 1 5/2 *").nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 5, 1, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 7, 1, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 1 5/2 *").nextTimeAfter(after).equals(expected));

        // if rolling over year then reset month field (cron rules - increments only affect own field)
        after = LocalDateTime.of(2012, 5, 1, 0, 0, 0, 0);
        expected = LocalDateTime.of(2013, 5, 1, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 1 5/10 *").nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_month_list() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0 0 1 3,7,12 *");

        LocalDateTime after = LocalDateTime.of(2012, 2, 12, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 3, 1, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 3, 1, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 7, 1, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 7, 1, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 12, 1, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_month_list_by_name() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0 0 1 MAR,JUL,DEC *");

        LocalDateTime after = LocalDateTime.of(2012, 2, 12, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 3, 1, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 3, 1, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 7, 1, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 7, 1, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 12, 1, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test()
    public void check_month_invalid_modifier() throws Exception {
        try {
            new CronExpression("0 0 0 1 ? *");
        } catch (ParseException e) {
            return;
        }
        Assertions.fail("Must throw IllegalArgumentException");
    }

    @Test
    public void check_dayOfWeek_number() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("* * * ? * TUE *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 1, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 3, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 4, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 10, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 12, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 17, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 18, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 24, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }


    @Test
    public void check_dayOfWeek_list() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0 0 ? * SUN,THU,SAT *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 1, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 5, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 2, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 5, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 6, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 7, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_dayOfWeek_list_by_name() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("0 0 0 ? * SUN,MON,FRI *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 1, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 2, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 2, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 6, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 6, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 8, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }

    @Test
    public void check_dayOfWeek_last_friday_in_month() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("* * * ? * 6L *");

        LocalDateTime after = LocalDateTime.of(2012, 4, 1, 1, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 27, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 28, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 5, 25, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 2, 6, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 2, 24, 0, 0, 0, 0);
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 2, 6, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 2, 24, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("* * * ? * FRIL * ").nextTimeAfter(after).equals(expected));
    }

    @Test()
    public void check_dayOfWeek_invalid_modifier() throws Exception {
        try {
            new CronExpression("0 0 0 * * 5W");
        } catch (ParseException e) {
            return;
        }
        Assertions.fail("Must throw IllegalArgumentException");
    }

    @Test()
    public void check_dayOfWeek_invalid_increment_modifier() throws Exception {
        try {
            new CronExpression("0 0 0 * * 5?3");
        } catch (ParseException e) {
            return;
        }
        Assertions.fail("Must throw IllegalArgumentException");
    }


    @Test
    public void check_dayOfWeek_nth_day_in_month() throws Exception {
        LocalDateTime after = LocalDateTime.of(2012, 4, 1, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2012, 4, 20, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 ? * 6#3 *").nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 20, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 5, 18, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 ? * 6#3 *").nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 3, 30, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 4, 1, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 ? * 1#1 *").nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 4, 1, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 5, 6, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 ? * 1#1 *").nextTimeAfter(after).equals(expected));

        after = LocalDateTime.of(2012, 2, 6, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 2, 29, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 ? * 4#5 *").nextTimeAfter(after).equals(expected)); // leapday

        after = LocalDateTime.of(2012, 2, 6, 0, 0, 0, 0);
        expected = LocalDateTime.of(2012, 2, 29, 0, 0, 0, 0);
        assertTrue(new LocalCronExpression("0 0 0 ? * 4#5 *").nextTimeAfter(after).equals(expected)); // leapday
    }

    @Test()
    public void shall_not_not_support_rolling_period() throws Exception {
        try {
            new CronExpression("* * 5-1 * * *");
        } catch (ParseException e) {
            return;
        }
        Assertions.fail("Must throw IllegalArgumentException");
    }


    @Test
    public void test_default_barrier() throws Exception {
        LocalCronExpression cronExpr = new LocalCronExpression("* * * 29 2 *");

        LocalDateTime after = LocalDateTime.of(2012, 3, 1, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2016, 2, 29, 0, 0, 0, 0);
        // the default barrier is 4 years - so leap years are considered.
        assertTrue(cronExpr.nextTimeAfter(after).equals(expected));
    }


    @Test
    public void test_without_seconds() throws Exception {
        LocalDateTime after = LocalDateTime.of(2012, 3, 1, 0, 0, 0, 0);
        LocalDateTime expected = LocalDateTime.of(2016, 2, 29, 0, 0, 0, 0);
        assertTrue(LocalCronExpression.createWithoutSeconds("* * 29 2 *").nextTimeAfter(after).equals(expected));
    }


//    @Test
//    public void CronExpressionAfterTest() throws Exception {
//        CronExpression cronExpr = new CronExpression("0 0 0 9W * ?");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        // Nachfolgendes Datum als Basis
//        Date after = sdf.parse("2012-09-01"); // 1. September 2012
//
//        Date expected = sdf.parse("2012-09-03");;
//        assertEquals(expected, cronExpr.getTimeAfter(after));
//
//
//    }


}
