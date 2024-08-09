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

import de.longri.crontab.type.JobType;
import de.longri.utils.CancelTimeOutJob;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * The Job class represents an abstract job that can be scheduled and executed within the CrontabScheduler.
 * It extends the CancelTimeOutJob class, providing additional functionality specific to this application.
 */
public abstract class Job extends CancelTimeOutJob {

    /**
     * Constructs a Job with the given job name.
     *
     * @param jobName the name of the job
     */
    protected Job(String jobName) {
        super(jobName);
    }

    /**
     * Constructs a Job with the given job name, timeout duration, and time unit.
     *
     * @param jobName the name of the job
     * @param timeout the timeout duration for the job
     * @param timeUnit the time unit for the timeout duration
     */
    protected Job(String jobName, long timeout, TimeUnit timeUnit) {
        super(jobName, timeout, timeUnit);
    }

    /**
     * Parses the provided argument string into an array of arguments.
     *
     * @param arg the argument string to parse
     * @return an array of arguments parsed from the provided string
     */
    protected static String[] getArgs(String arg) {
        String[] args = arg.split(" ");
        ArrayList<String> list = new ArrayList<>();
        StringBuilder sb = null;
        for (String a : args) {
            if (sb != null) {
                sb.append(a.replace("\"", "")).append(" ");
                if (a.endsWith("\"")) {
                    list.add(sb.toString().trim());
                    sb = null;
                }
            } else if (a.startsWith("\"")) {
                if (a.endsWith("\"")) {
                    list.add(a.replace("\"", ""));
                } else {
                    sb = new StringBuilder();
                    sb.append(a.replace("\"", "")).append(" ");
                }

            } else {
                list.add(a);
            }
        }

        int l = list.size();

        return list.toArray(new String[l]);
    }

    /**
     * Gets the type of the job.
     *
     * @return the type of the job
     */
    public abstract JobType getType();

    /**
     * Gets the arguments for the job as a string.
     *
     * @return the arguments for the job
     */
    public abstract String getArgs();

    /**
     * Sets the arguments for the job.
     *
     * @param args the arguments to set for the job
     */
    public abstract void setArgs(String args);
}