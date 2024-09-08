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
package de.longri.crontab.type;


import de.longri.crontab.Job;
import de.longri.crontab.Job_Logging;

/**
 * The JobType_Logging class represents a specific type of job that performs logging operations.
 * It extends the JobType class, providing implementations for creating job instances and templates.
 */
public class JobType_Logging extends JobType {

    /**
     * The default message for logging jobs.
     */
    public static final String DEFAULT_MSG = "New Message";

    /**
     * Constructs a JobType_Logging with the name "LOGGING".
     */
    public JobType_Logging() {
        super("LOGGING");
    }

    /**
     * Gets a template job for the logging job type.
     *
     * @return a template job for the logging job type
     */
    @Override
    public Job getTemplate() {
        return new Job_Logging(DEFAULT_NAME, DEFAULT_MSG);
    }

    /**
     * Gets a new instance of a logging job with the specified name and arguments.
     *
     * @param name the name of the job
     * @param args the arguments for the job
     * @return a new instance of a logging job
     * @throws Exception if there is an error creating job instance
     */
    @Override
    public Job getJobInstance(String name, String args) throws Exception {
        return new Job_Logging(name, args);
    }
}
