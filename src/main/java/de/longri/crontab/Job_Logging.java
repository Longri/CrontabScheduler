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

public class Job_Logging extends Job {

    String MSG;

    public Job_Logging(String jobName, String msg) {
        super(jobName);
        this.MSG = msg;
    }

    @Override
    public String getArgs() {
        return MSG;
    }

    @Override
    public void setArgs(String args) {
        MSG = args;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void work() {
        log.debug(MSG);
    }

    @Override
    public JobType getType() {
        return JobType.getFromString("LOGGING");
    }
}
