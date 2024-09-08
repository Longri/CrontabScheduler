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

import de.longri.crontab.Cronjob;
import de.longri.crontab.Job;
import de.longri.crontab.LocalCronExpression;
import de.longri.crontab.type.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

class CronjobTest {

    @BeforeAll
    static void setUp() {

    }

    @Test
    void isRunNext10Min() throws ParseException {
        if (EXCLUDE.LongTimeTests()) return;
        AtomicBoolean runChk = new AtomicBoolean(false);

        LocalDateTime now = LocalDateTime.now();
        LocalCronExpression cronExpr = new LocalCronExpression("0 0/3 * ? * * *");
        LocalDateTime next = cronExpr.nextTimeAfter(now);
        Cronjob cronjob = new Cronjob("0 0/3 * ? * * * : Job", new Job("TestName") {


            @Override
            public String getArgs() {
                return "TestArgs";
            }

            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void setArgs(String args) {

            }

            @Override
            public void work() {
                runChk.set(true);
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob.isRunNextMin(3));
        cronjob.waitAndRun(3);

        now = LocalDateTime.now();
        next = next.truncatedTo(ChronoUnit.SECONDS);
        next = next.truncatedTo(ChronoUnit.MILLIS);
        now = now.truncatedTo(ChronoUnit.SECONDS);
        now = now.truncatedTo(ChronoUnit.MILLIS);
        assertEquals(now, next);
        assertTrue(runChk.get());
    }

    @Test
    void testSingleExecution() throws ParseException, InterruptedException {
        if (EXCLUDE.LongTimeTests()) return;
        LocalDateTime start = LocalDateTime.now().plusMinutes(11);

        // remove last preferences Value
        Preferences.userRoot().node(Cronjob.class.getName()).put("CronJob0 0/6 * ? * * * : Job321", "");

        AtomicInteger runChk = new AtomicInteger(0);
        AtomicInteger ready = new AtomicInteger(0);
        Cronjob cronjob = new Cronjob("0 0/6 * ? * * * : Job", new Job("TestName") {

            @Override
            public void setArgs(String args) {

            }

            @Override
            public String getArgs() {
                return "TestArgs";
            }


            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void work() {
                runChk.incrementAndGet();
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob.isRunNextMin(6));

        Cronjob cronjob2 = new Cronjob("0 0/6 * ? * * * : Job", new Job("TestName") {

            @Override
            public void setArgs(String args) {

            }

            @Override
            public String getArgs() {
                return "TestArgs";
            }


            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void work() {
                runChk.incrementAndGet();
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob2.isRunNextMin(6));

        Cronjob cronjob3 = new Cronjob("0 0/6 * ? * * * : Job", new Job("TestName") {

            @Override
            public void setArgs(String args) {

            }

            @Override
            public String getArgs() {
                return "TestArgs";
            }


            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void work() {
                runChk.incrementAndGet();
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob3.isRunNextMin(6));

        new Thread(new Runnable() {
            @Override
            public void run() {
                cronjob.waitAndRun(6);
                ready.incrementAndGet();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cronjob2.waitAndRun(6);
                ready.incrementAndGet();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cronjob3.waitAndRun(6);
                ready.incrementAndGet();
            }
        }).start();

        // wait for end of all threads
        while (ready.get() < 3) {
            Thread.sleep(250);
            if (LocalDateTime.now().isAfter(start)) {
                fail("Time out");
            }
        }

        //only one Job must execute
        assertEquals(1, runChk.get());
    }

    @Test
    void testSingleExecution5Times() throws ParseException, InterruptedException {
        if (EXCLUDE.LongTimeTests()) return;
        LocalDateTime start = LocalDateTime.now().plusMinutes(13);

        // remove last preferences Value
        Preferences.userRoot().node(Cronjob.class.getName()).put("CronJob0 0/2 * ? * * * : Job421", "");

        AtomicInteger runChk = new AtomicInteger(0);
        AtomicInteger ready = new AtomicInteger(0);
        Cronjob cronjob = new Cronjob("0 0/2 * ? * * * : Job", new Job("TestName") {

            @Override
            public void setArgs(String args) {

            }

            @Override
            public String getArgs() {
                return "TestArgs";
            }


            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void work() {
                runChk.incrementAndGet();
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob.isRunNextMin(10));

        Cronjob cronjob2 = new Cronjob("0 0/2 * ? * * * : Job", new Job("TestName") {

            @Override
            public void setArgs(String args) {

            }

            @Override
            public String getArgs() {
                return "TestArgs";
            }


            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void work() {
                runChk.incrementAndGet();
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob2.isRunNextMin(10));

        Cronjob cronjob3 = new Cronjob("0 0/2 * ? * * * : Job", new Job("TestName") {

            @Override
            public void setArgs(String args) {

            }

            @Override
            public String getArgs() {
                return "TestArgs";
            }

            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void work() {
                runChk.incrementAndGet();
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob3.isRunNextMin(10));

        new Thread(new Runnable() {
            @Override
            public void run() {
                cronjob.waitAndRun(10);
                ready.incrementAndGet();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cronjob2.waitAndRun(10);
                ready.incrementAndGet();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cronjob3.waitAndRun(10);
                ready.incrementAndGet();
            }
        }).start();

        // wait for end of all threads
        while (ready.get() < 3) {
            Thread.sleep(250);
            if (LocalDateTime.now().isAfter(start)) {
                fail("Time out");
            }
        }

        //Job must 5 times execute in 10 min
        assertEquals(5, runChk.get());
    }

    @Test
    void testSingleExecutionEveryMinute() throws ParseException, InterruptedException {
        if (EXCLUDE.LongTimeTests()) return;
        LocalDateTime start = LocalDateTime.now().plusMinutes(13);

        // remove last preferences Value
        Preferences.userRoot().node(Cronjob.class.getName()).put("CronJob0 0/1 * ? * * * : Job421", "");

        AtomicInteger runChk = new AtomicInteger(0);
        AtomicInteger ready = new AtomicInteger(0);
        Cronjob cronjob = new Cronjob("0 0/1 * ? * * * : Job", new Job("TestName") {

            @Override
            public void setArgs(String args) {

            }

            @Override
            public String getArgs() {
                return "TestArgs";
            }


            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void work() {
                runChk.incrementAndGet();
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob.isRunNextMin(10));

        Cronjob cronjob2 = new Cronjob("0 0/1 * ? * * * : Job", new Job("TestName") {

            @Override
            public void setArgs(String args) {

            }

            @Override
            public String getArgs() {
                return "TestArgs";
            }


            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void work() {
                runChk.incrementAndGet();
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob2.isRunNextMin(10));

        Cronjob cronjob3 = new Cronjob("0 0/1 * ? * * * : Job", new Job("TestName") {

            @Override
            public void setArgs(String args) {

            }

            @Override
            public String getArgs() {
                return "TestArgs";
            }


            @Override
            public void setName(String name) {
                // test, do nothing
            }

            @Override
            public void work() {
                runChk.incrementAndGet();
            }

            @Override
            public JobType getType() {
                return JobType.UNKNOWN;
            }
        });
        assertTrue(cronjob3.isRunNextMin(10));

        new Thread(new Runnable() {
            @Override
            public void run() {
                cronjob.waitAndRun(10);
                ready.incrementAndGet();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cronjob2.waitAndRun(10);
                ready.incrementAndGet();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                cronjob3.waitAndRun(10);
                ready.incrementAndGet();
            }
        }).start();

        // wait for end of all threads
        while (ready.get() < 3) {
            Thread.sleep(250);
            if (LocalDateTime.now().isAfter(start)) {
                fail("Time out");
            }
        }

        //Job must 10 times execute in 10 min
        assertEquals(10, runChk.get());
    }
}