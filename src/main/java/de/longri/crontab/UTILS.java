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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The UTILS class provides utility methods and constants used across the CrontabScheduler application.
 */
public class UTILS {

    /**
     * A DateTimeFormatter to format dates and times in the pattern "yyyy-MM-dd HH:mm".
     * This is used throughout the application to ensure consistent date-time formatting.
     */
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Parses a date-time string into a LocalDateTime object.
     *
     * @param dateTimeString the date-time string to parse
     * @return the parsed LocalDateTime object
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    /**
     * Formats a LocalDateTime object into a string.
     *
     * @param localDateTime the LocalDateTime object to format
     * @return the formatted date-time string
     */
    public static String format(LocalDateTime localDateTime) {
        return formatter.format(localDateTime);
    }



}