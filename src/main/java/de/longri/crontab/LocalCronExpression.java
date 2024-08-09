/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of fxutils.
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

import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class LocalCronExpression {

    public CronExpression EXPRESSION;

    /**
     * Constructs a new <CODE>CronExpression</CODE> based on the specified
     * parameter.
     *
     * @param cronExpression String representation of the cron expression the
     *                       new object should represent
     * @throws ParseException if the string expression cannot be parsed into a valid
     *                        <CODE>CronExpression</CODE>
     */
    public LocalCronExpression(String cronExpression) throws ParseException {

        String[] sa = cronExpression.split(" ");

        if (sa.length == 5) {
            cronExpression = "* " + cronExpression;
            sa = cronExpression.split(" ");
        }

        if (sa.length == 6) {
            cronExpression = sa[0] + " " + sa[1] + " " + sa[2] + " " + sa[3] + " " + sa[4] + " ? " + sa[5] + " ";
        }

        EXPRESSION = new CronExpression(cronExpression);
    }

    /**
     * Constructs a new {@code CronExpression} as a copy of an existing
     * instance.
     *
     * @param expression The existing cron expression to be copied
     */
    public LocalCronExpression(CronExpression expression) {
        EXPRESSION = new CronExpression(expression);
    }

    public static LocalCronExpression createWithoutSeconds(String s) throws ParseException {
        return new LocalCronExpression(s);
    }

    public LocalDateTime nextTimeAfter(LocalDateTime after) {
        return toDate(EXPRESSION.getTimeAfter(toDate(after)));
    }

    public LocalDateTime nextTimeAfter(LocalDateTime after, int i) {
        return null;
    }

    public LocalDateTime nextTimeAfter(LocalDateTime after, LocalDateTime barier) {
        return null;
    }

    public String toString() {
        return EXPRESSION.toString();
    }

    static Date toDate(LocalDateTime ldt) {
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    static LocalDateTime toDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public void set(String expression) throws ParseException {
        EXPRESSION = new CronExpression(expression);
    }
}
