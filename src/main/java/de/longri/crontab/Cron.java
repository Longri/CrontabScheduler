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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

/**
 * The Cron class holds the scheduling information for the last and next schedule executions.
 * It provides functionality to store, retrieve, and format scheduling times.
 */
public class Cron {

    /**
     * Preferences node for storing cron-related data.
     */
    private static final Preferences CRON_PREF = Preferences.userRoot().node(Cron.class.getName());



    /**
     * Retrieves the last execution time for the specified cron job.
     *
     * @param cron the cron job for which to retrieve the last execution time
     * @return the last execution time as a LocalDateTime
     */
    private static LocalDateTime getLastCall(Cron cron) {
        String last = CRON_PREF.get(cron.CRONID, null);
        if (last == null) return null;
        return UTILS.parseDateTime(last);
    }


    /**
     * Unique identifier for the cron job.
     */
    public final String CRONID;

    /**
     * Command associated with the cron job.
     */
    public final String COMMAND;

    /**
     * Constructs a Cron instance with the specified cron expression and command.
     *
     * @param cronExpression the cron expression
     * @param command the command to be executed
     */
    public Cron(String cronExpression, String command) {
        this.COMMAND = command;
        this.CRONID = cronExpression + command;
    }


}
