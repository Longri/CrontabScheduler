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

import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public abstract class CancelTimeOutJob implements Runnable {

    DoubleProperty progressProperty = new SimpleDoubleProperty(0);
    StringProperty progressMsgProperty = new SimpleStringProperty("");

    final long TIME_OUT;
    final TimeUnit TIME_OUT_UNIT;
    protected Logger log;
    String JOB_NAME;
    ExecutorService executor;
    Future<String> future;
    long workTimeStart;
    Duration workDuration = null;
    private final BooleanProperty readyProperty = new SimpleBooleanProperty(false);

    protected CancelTimeOutJob(String jobName) {
        JOB_NAME = jobName;
        log = LoggerFactory.getLogger("JOB:" + jobName);
        TIME_OUT = 0;
        TIME_OUT_UNIT = null;
    }

    protected CancelTimeOutJob(String jobName, Duration duration) {
        JOB_NAME = jobName;
        log = LoggerFactory.getLogger("JOB:" + jobName);
        TIME_OUT = duration.MILLIS;
        TIME_OUT_UNIT = TimeUnit.MILLISECONDS;
    }

    protected CancelTimeOutJob(String jobName, long timout, TimeUnit timeUnit) {
        JOB_NAME = jobName;
        log = LoggerFactory.getLogger("JOB:" + jobName);
        TIME_OUT = timout;
        TIME_OUT_UNIT = timeUnit;
    }

    public String getName() {
        return JOB_NAME;
    }

    public void setName(String name) {
        JOB_NAME = name;
    }


    public final void run() {

        executor = Executors.newSingleThreadExecutor();
        future = executor.submit(new TimeOutTask());
        Throwable runtimeException = null;
        workTimeStart = System.currentTimeMillis();
        if (TIME_OUT_UNIT == null) {
            try {
                log.debug("Work Started with out timeout");
                log.debug(future.get(Long.MAX_VALUE, TimeUnit.DAYS));
                calcWorkDuration();
                log.debug("Work Finished after {}!", workDuration);
                workFinished();
            } catch (TimeoutException | InterruptedException | ExecutionException | CancellationException e) {
                future.cancel(true);
                calcWorkDuration();
                log.debug("Work Terminated after {}!", workDuration);
                if (e instanceof ExecutionException) runtimeException = e.getCause();
            }
            executor.shutdownNow();
        } else {
            try {
                log.debug("Work Started with a timeout value of {} {}", TIME_OUT, TIME_OUT_UNIT);
                log.debug(future.get(TIME_OUT, TIME_OUT_UNIT));
                calcWorkDuration();
                log.debug("Work Finished after {}!", workDuration);
                workFinished();
            } catch (TimeoutException | InterruptedException | ExecutionException | CancellationException e) {
                future.cancel(true);
                calcWorkDuration();
                log.debug("Work Terminated after {}!", workDuration);
                if (e instanceof TimeoutException) workTimeOut();
                if (e instanceof ExecutionException) runtimeException = e.getCause();
            }
            executor.shutdownNow();
        }

        executor = null;
        future = null;
        workReady();
        if (runtimeException != null) {
            runtimeException.printStackTrace();
            throw new RuntimeException(runtimeException);
        }
    }

    public void cancel() {
        if (executor == null || future == null) return;
        future.cancel(true);
        log.debug("Work canceled.");
        workCanceled();
    }

    private void calcWorkDuration() {
        long time = System.currentTimeMillis() - workTimeStart;
        workDuration = new Duration(time, true);
    }

    protected abstract void work() throws RuntimeException, Exception;

    private void workReady() {
        readyProperty.set(true);
    }


    /**
     * Called if work finish bat not canceled
     */
    public void workFinished() {
    }

    /**
     * Called if work canceled and not finish
     */
    public void workCanceled() {
    }

    /**
     * Called if work canceled by time out
     */
    public void workTimeOut() {
    }

    public void startNewThread() {
        new Thread(this).start();
    }

    protected void updateMessage(String message) {
        setProgressMsg(message);
    }

    protected void updateProgress(double value, double maxValue) {
        setProgress(value, maxValue);
    }

    public BooleanProperty getReadyProperty() {
        return readyProperty;
    }

    class TimeOutTask implements Callable<String> {
        @Override
        public String call() throws Exception {
            work();
            return "Ready!";
        }
    }

    public DoubleProperty getProgressProperty() {
        return progressProperty;
    }

    public void setProgress(double val, double max) {
        double x = (100.0 * val) / max;
        progressProperty.set(x / 100.0);
    }

    public double getProgress() {
        return progressProperty.get();
    }

    public StringProperty getProgressMsgProperty() {
        return progressMsgProperty;
    }

    public void setProgressMsg(String val) {
        progressMsgProperty.set(val);
    }

    public String getProgressMsg() {
        return progressMsgProperty.get();
    }
}
