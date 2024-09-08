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

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DurationTest {

    @Test
    void testEquals() {

        Duration duration1 = new Duration(1000);
        Duration duration2 = new Duration(1, TimeUnit.SECONDS);
        assertEquals(duration1, duration2);

    }

    @Test
    void testToString() {
        Duration duration1 = new Duration(1000);
        assertEquals("Duration: 1000 MILLISECONDS", duration1.toString());

        Duration duration2 = new Duration(1, TimeUnit.SECONDS);
        assertEquals("Duration: 1 SECONDS", duration2.toString());

        Duration duration3 = new Duration(23, TimeUnit.HOURS);
        assertEquals("Duration: 23 HOURS", duration3.toString());
    }

    @Test
    void autoUnitConstructor() {
        assertEquals("Duration: 250 MILLISECONDS", new Duration(250, true).toString());
        assertEquals("Duration: 2.5 SECONDS", new Duration(2500, true).toString());
        assertEquals("Duration: 3.15 MINUTES", new Duration(195000, true).toString());
    }
}