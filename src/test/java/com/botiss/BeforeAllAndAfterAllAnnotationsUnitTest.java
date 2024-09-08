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
package com.botiss;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;

public class BeforeAllAndAfterAllAnnotationsUnitTest {

    static Logger log;

    @BeforeAll
    public static void setup() {

    }


    @AfterAll
    public static void tearDown() {
        if (log != null) log.debug("tearDown JUnit Tests");
    }

}