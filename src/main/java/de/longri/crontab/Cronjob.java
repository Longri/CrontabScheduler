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


import de.longri.crontab.type.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The Cronjob class represents a scheduled job that runs based on a specified cron expression.
 * It provides functionality for executing, retrieving, and managing scheduled jobs.
 */
public class Cronjob {
    /**
     * Separator used for serializing cron job data.
     */
    final static String SERIALIZE_SEPARATOR = ";";

    /**
     * Logger for logging cron job activities.
     */
    final static Logger log = LoggerFactory.getLogger(Cronjob.class);

    /**
     * The cron expression that defines the schedule for this cron job.
     */
    final public LocalCronExpression cronExpression;

    /**
     * The job to be executed according to the cron schedule.
     */
    public final Job job;

    /**
     * Preference identifier for storing cron job data.
     */
    final String prefId;

    /**
     * Preferences node for storing cron job data.
     */
    final Preferences prefs;

    /**
     * Constructs a Cronjob instance with the specified cron expression and job.
     *
     * @param cron the cron expression
     * @param job the job to be executed
     * @throws ParseException if there is an error parsing the cron expression
     */
    public Cronjob(String cron, Job job) throws ParseException {
        this.job = job;
        String[] jobconf = cron.split(SERIALIZE_SEPARATOR);
        this.cronExpression = new LocalCronExpression(jobconf[0]);
        this.prefId = "CronJob" + cron + job.getName();
        this.prefs = Preferences.userRoot().node(this.getClass().getName());
    }

    /**
     * Retrieves the last execution time for this cron job.
     *
     * @return the last execution time as a string
     */
    public String getLastExecution() {
        return prefs.get(this.prefId, "");
    }

    /**
     * Creates a Cronjob instance from an initialization string.
     *
     * @param jobString the initialization string representing the cron job
     * @return the created Cronjob instance
     * @throws Exception if there is an error parsing the initialization string
     */
    public static Cronjob getFromIniString(String jobString) throws Exception {
        if (jobString == null) throw new RuntimeException("JobString can't be NULL");
        String[] arr = jobString.split(SERIALIZE_SEPARATOR);
        JobType type = JobType.getFromString(arr[1].trim().toUpperCase(Locale.ROOT));
        if (type == null) return null;

        return new Cronjob(arr[0], type.getJobInstance(arr[2].trim(), arr[3].trim()));
    }

    /**
     * Retrieves the type of the job.
     *
     * @return the job type
     */
    public JobType getType() {
        return this.job.getType();
    }

    /**
     * Checks if the job should run in the next specified minutes.
     *
     * @param min the number of minutes to check
     * @return true if the job should run in the next specified minutes, false otherwise
     */
    public boolean isRunNextMin(int min) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inXmin = now.plusMinutes(min);
        LocalDateTime ldt = cronExpression.nextTimeAfter(now);
        return ldt.isBefore(inXmin);
    }

    /**
     * Waits for the specified number of minutes and then runs the job.
     *
     * @param waitMinute the number of minutes to wait
     */
    public void waitAndRun(int waitMinute) {
        if (this.job == null) return;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime waitEndTime = now.plusMinutes(waitMinute);

        LocalDateTime next = cronExpression.nextTimeAfter(now);
        if (next.isAfter(waitEndTime)) {
            log.debug("Next execution time is after WaitTime => RETURN");
            return;
        }

        int count = executionCountNextMinute(waitMinute);
        for (int i = 0; i < count; i++) {
            now = LocalDateTime.now();
            next = cronExpression.nextTimeAfter(now);
            if (next.isAfter(waitEndTime)) {
                log.debug("Next execution time is after WaitTime => RETURN");
                return;
            }

            if (blockExecution(next)) {
                log.debug("This Job is registered for this execution time, so block this execution");
                return;
            }

            log.info("wait for start job {} at {}", job.getName(),getNextExecution());
            while (next.isAfter(LocalDateTime.now())) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            log.debug("execution job {} at new Thread", job.getName());
            AtomicBoolean WAIT = new AtomicBoolean(true);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        job.run();
                        WAIT.set(false);
                    } catch (Exception e) {
                        WAIT.set(false);
                        log.error("Running exception", e);
                    }
                }
            });
            thread.start();

            log.debug("Wait for execution job {} ", job.getName());
            while (WAIT.get()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.debug("Job {} ready executed", job.getName());
        }

    }

    /**
     * Retrieves the number of times the job will execute in the next specified minutes.
     *
     * @param minute the number of minutes to check
     * @return the count of executions in the next specified minutes
     */
    public int executionCountNextMinute(int minute) {
        LocalDateTime next = LocalDateTime.now();
        LocalDateTime inMin = next.plusMinutes(minute);
        int count = 0;
        while (true) {
            next = cronExpression.nextTimeAfter(next);
            if (next.isAfter(inMin)) {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * Blocks execution of the job until the specified time.
     *
     * @param next the time to allow execution
     * @return true if execution is blocked, false otherwise
     */
    private boolean blockExecution(LocalDateTime next) {
        synchronized (log) {
            String formattedDateTime = next.format(UTILS.formatter);
            String lastStr = prefs.get(this.prefId, "");
            if (formattedDateTime.equals(lastStr)) {
                log.debug("BLOCK => Last {} == Next {}", lastStr, formattedDateTime);
                return true;
            }

            log.debug("RUN => Last {} == Next {}", lastStr, formattedDateTime);

            //register execution time
            prefs.put(this.prefId, formattedDateTime);
            try {
                prefs.flush();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Retrieves the name of the job.
     *
     * @return the job name
     */
    public String getName() {
        return this.job.getName();
    }

    /**
     * Retrieves the next execution time for this cron job.
     *
     * @return the next execution time as a string
     */
    public String getNextExecution() {
        return cronExpression.nextTimeAfter(LocalDateTime.now()).format(UTILS.formatter);
    }

    /**
     * Converts this Cronjob instance to an initialization string.
     *
     * @return the initialization string representing this cron job
     */
    public String toIniString() {
        StringBuilder sb = new StringBuilder(cronExpression.toString());
        sb.append("; ").append(this.job.getType().toIniString());
        sb.append(" ;").append(this.job.getName());
        sb.append(" ;").append(this.job.getArgs());
        return sb.toString();
    }

    /**
     * Converts this Cronjob instance to a string.
     *
     * @return the string representation of this cron job
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("CronJob : ");
        sb.append(this.job.getName()).append(" | ");
        sb.append(cronExpression).append(" | ");
        sb.append("LastExe: ").append(getLastExecution()).append(" | ");
        sb.append("NextExe: ").append(getNextExecution()).append(" | ");
        return sb.toString();
    }

    /**
     * Sets the name of the job.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.job.setName(name);
    }

    /**
     * Retrieves the arguments for the job.
     *
     * @return the job arguments
     */
    public String getArgs() {
        return this.job.getArgs();
    }

    /**
     * Sets the arguments for the job.
     *
     * @param args the arguments to set
     */
    public void setArgs(String args) {
        this.job.setArgs(args);
    }

    /**
     * Retrieves the job associated with this cron job.
     *
     * @return the job
     */
    public Job getJob() {
        return this.job;
    }
}
