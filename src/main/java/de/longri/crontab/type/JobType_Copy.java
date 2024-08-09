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
package de.longri.crontab.type;

import de.longri.crontab.Job;
import de.longri.crontab.Job_Copy;
import org.apache.commons.cli.ParseException;

/**
 * The JobType_Copy class represents a specific type of job that performs copy operations.
 * It extends the JobType class, providing implementations for creating job instances and templates.
 */
public class JobType_Copy extends JobType {

    /**
     * The default arguments for copy jobs.
     */
    public static final String DEFAULT_COPY_ARGS = "-s \"\" -t\"\"";

    /**
     * Constructs a JobType_Copy with the name "COPY".
     */
    public JobType_Copy() {
        super("COPY");
    }

    /**
     * Gets a template job for the copy job type.
     *
     * @return a template job for the copy job type
     * @throws java.text.ParseException if there is an error parsing job parameters
     */
    @Override
    public Job getTemplate() throws ParseException {
        return new Job_Copy(
                DEFAULT_NAME, DEFAULT_COPY_ARGS);
    }

    /**
     * Gets a new instance of a copy job with the specified name and arguments.
     *
     * @param name the name of the job
     * @param args the arguments for the job
     * @return a new instance of a copy job
     * @throws java.text.ParseException if there is an error parsing job parameters
     */
    @Override
    public Job getJobInstance(String name, String args) throws ParseException {
        return new Job_Copy(name, args);
    }
}
