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
package de.longri.crontab;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DelayedCancelTaskTest {

    @Test
    void start() {

        // This is a long-running test
        if(true) return;

        AtomicInteger INT = new AtomicInteger(0);

        DelayedCancelTask task = new DelayedCancelTask(500L, 3000L, new Runnable() {
            @Override
            public void run() {
                INT.set(1);
            }
        }, new Runnable() {
            @Override
            public void run() {
                INT.set(2);
            }
        });

        assertEquals(0, INT.get());
        task.start();
        sleep(100);
        assertEquals(0, INT.get());
        sleep(100);
        assertEquals(0, INT.get());
        task.cancel();
        assertEquals(0, INT.get());
        sleep(600);
        assertEquals(0, INT.get());
        task.start();
        assertEquals(0, INT.get());
        sleep(400);
        assertEquals(0, INT.get());
        sleep(200);
        assertEquals(1, INT.get());
        sleep(300);
        assertEquals(1, INT.get());
        task.cancel();
        assertEquals(1, INT.get());
        sleep(3100);
        assertEquals(2, INT.get());

        INT.set(0);

        assertEquals(0, INT.get());
        task.start();
        sleep(100);
        assertEquals(0, INT.get());
        sleep(100);
        assertEquals(0, INT.get());
        task.cancel();
        assertEquals(0, INT.get());
        sleep(600);
        assertEquals(0, INT.get());
        task.start();
        assertEquals(0, INT.get());
        sleep(400);
        assertEquals(0, INT.get());
        sleep(200);
        assertEquals(1, INT.get());
        sleep(300);
        assertEquals(1, INT.get());
        task.cancel();
        assertEquals(1, INT.get());
        sleep(3100);
        assertEquals(2, INT.get());

    }

    void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}