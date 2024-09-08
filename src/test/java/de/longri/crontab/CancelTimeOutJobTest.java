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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CancelTimeOutJobTest {

    @BeforeAll
    static void beforeAll() {

    }


    @Test
    void cancel() throws InterruptedException {
        // run a job without time out settings and cancel the work after a second
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicBoolean notCancel = new AtomicBoolean(false);

        final AtomicBoolean timeOutCalled = new AtomicBoolean(false);
        final AtomicBoolean cancelCalled = new AtomicBoolean(false);
        final AtomicBoolean finishCalled = new AtomicBoolean(false);


        CancelTimeOutJob job = new CancelTimeOutJob("cancel test job") {
            @Override
            protected void work() {
                while (count.incrementAndGet() < 100) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                notCancel.set(true);
            }

            /**
             * Called if work finish bat not canceled
             */
            public void workFinished() {
                finishCalled.set(true);
            }

            /**
             * Called if work canceled and not finish
             */
            public void workCanceled() {
                cancelCalled.set(true);
            }

            /**
             * Called if work canceled by time out
             */
            public void workTimeOut() {
                timeOutCalled.set(true);
            }
        };

        job.startNewThread();

        Thread.sleep(200);
        job.cancel();
        Thread.sleep(1000);
        assertFalse(notCancel.get(), "job was not canceled");
        assertEquals(2, count.get());

        assertTrue(cancelCalled.get());
        assertFalse(finishCalled.get());
        assertFalse(timeOutCalled.get());
    }

    @Test
    void workTimeOut() throws InterruptedException {
        final AtomicInteger count = new AtomicInteger(0);

        final AtomicBoolean timeOutCalled = new AtomicBoolean(false);
        final AtomicBoolean cancelCalled = new AtomicBoolean(false);
        final AtomicBoolean finishCalled = new AtomicBoolean(false);


        CancelTimeOutJob job = new CancelTimeOutJob("timeout test job", new Duration(1000)) {
            @Override
            protected void work() {
                while (count.incrementAndGet() < 100) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            /**
             * Called if work finish bat not canceled
             */
            public void workFinished() {
                finishCalled.set(true);
            }

            /**
             * Called if work canceled and not finish
             */
            public void workCanceled() {
                cancelCalled.set(true);
            }

            /**
             * Called if work canceled by time out
             */
            public void workTimeOut() {
                timeOutCalled.set(true);
            }
        };

        job.startNewThread();
        Thread.sleep(1100);
        assertEquals(10, count.get());
        assertFalse(cancelCalled.get());
        assertFalse(finishCalled.get());
        assertTrue(timeOutCalled.get());
    }

    @Test
    void work() throws InterruptedException {
        final AtomicInteger count = new AtomicInteger(0);

        final AtomicBoolean timeOutCalled = new AtomicBoolean(false);
        final AtomicBoolean cancelCalled = new AtomicBoolean(false);
        final AtomicBoolean finishCalled = new AtomicBoolean(false);


        CancelTimeOutJob job = new CancelTimeOutJob("finish test job", new Duration(1000)) {
            @Override
            protected void work() {
                while (count.incrementAndGet() < 5) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            /**
             * Called if work finish bat not canceled
             */
            public void workFinished() {
                finishCalled.set(true);
            }

            /**
             * Called if work canceled and not finish
             */
            public void workCanceled() {
                cancelCalled.set(true);
            }

            /**
             * Called if work canceled by time out
             */
            public void workTimeOut() {
                timeOutCalled.set(true);
            }
        };

        job.startNewThread();
        Thread.sleep(1100);
        assertEquals(5, count.get());
        assertFalse(cancelCalled.get());
        assertTrue(finishCalled.get());
        assertFalse(timeOutCalled.get());
    }

    @Test
    void progressWork() throws InterruptedException {
        final AtomicInteger count = new AtomicInteger(0);

        final AtomicBoolean timeOutCalled = new AtomicBoolean(false);
        final AtomicBoolean cancelCalled = new AtomicBoolean(false);
        final AtomicBoolean finishCalled = new AtomicBoolean(false);


        CancelTimeOutJob job = new CancelTimeOutJob("finish test job", new Duration(5000)) {
            @Override
            protected void work() {
                while (count.incrementAndGet() < 101) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    setProgress(count.get(), 100);
                }
            }

            /**
             * Called if work finish bat not canceled
             */
            public void workFinished() {
                finishCalled.set(true);
            }

            /**
             * Called if work canceled and not finish
             */
            public void workCanceled() {
                cancelCalled.set(true);
            }

            /**
             * Called if work canceled by time out
             */
            public void workTimeOut() {
                timeOutCalled.set(true);
            }
        };

        job.startNewThread();

        AtomicInteger expectCount = new AtomicInteger(0);
        Thread.sleep(5);
        while (expectCount.getAndIncrement() < 120 && !finishCalled.get()) {
            Thread.sleep(10);
            assertEquals(expectCount.get(), job.getProgress() * 100, 5);
        }

        Thread.sleep(100);
        assertEquals(101, count.get(), 0);
        assertFalse(cancelCalled.get());
        assertTrue(finishCalled.get());
        assertFalse(timeOutCalled.get());
    }
}