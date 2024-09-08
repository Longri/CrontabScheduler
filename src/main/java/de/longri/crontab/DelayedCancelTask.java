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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class DelayedCancelTask {
    private final static Logger log = LoggerFactory.getLogger(DelayedCancelTask.class);

    private final AtomicBoolean isRun = new AtomicBoolean(false);
    private final long delay;
    private final long minShowTime;
    private long runtime;
    private final Runnable runnable;
    private final Runnable cancelRunnable;
    private Timer timer;

    public DelayedCancelTask(Long delay, Long minShowTime, Runnable runnable, Runnable cancelRunnable) {
        this.runnable = runnable;
        this.cancelRunnable = cancelRunnable;
        this.delay = delay;
        this.minShowTime = minShowTime;
    }

    public void start() {
        synchronized (isRun) {
            this.timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runnable.run();
                    runtime = System.currentTimeMillis();
                    isRun.set(true);
                }
            }, delay);
        }
    }

    public void cancel() {
        synchronized (isRun) {
            if (timer != null) {
                timer.cancel();
                timer = null;
                if (isRun.get()) {
                    Thread thread = new Thread(() -> {
                        long now = System.currentTimeMillis();
                        long re = runtime + minShowTime;
                        log.debug("runtime: {} | minShowTime: {} | now: {} | result: {}", runtime, minShowTime, now, re > now);
                        while (re > now) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                //ignore
                            }
                            now = System.currentTimeMillis();
                        }
                        cancelRunnable.run();
                        isRun.set(false);
                    });
                    thread.start();
                }
            }
        }
    }

}
