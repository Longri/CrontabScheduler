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

import java.util.concurrent.TimeUnit;

public class Duration {

    public final long MILLIS;
    public final TimeUnit UNIT;

    public Duration(long duration) {
        this(duration, TimeUnit.MILLISECONDS);
    }

    public Duration(long duration, boolean autoUnit) {
        if (autoUnit) {
            this.MILLIS = duration;
            if (duration <= TimeUnit.SECONDS.toMillis(1)) {
                this.UNIT = TimeUnit.MILLISECONDS;
            } else if (duration <= TimeUnit.MINUTES.toMillis(1)) {
                this.UNIT = TimeUnit.SECONDS;
            } else if (duration <= TimeUnit.HOURS.toMillis(1)) {
                this.UNIT = TimeUnit.MINUTES;
            } else if (duration <= TimeUnit.DAYS.toMillis(1)) {
                this.UNIT = TimeUnit.HOURS;
            } else {
                this.UNIT = TimeUnit.MILLISECONDS;
            }
        } else {
            this.MILLIS = duration;
            this.UNIT = TimeUnit.MILLISECONDS;
        }
    }

    public Duration(long duration, TimeUnit timeUnit) {
        this.MILLIS = timeUnit.toMillis(duration);
        this.UNIT = timeUnit;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Duration)) return false;
        return ((Duration) other).MILLIS == this.MILLIS;
    }

    @Override
    public String toString() {
        long val = MILLIS / UNIT.toMillis(1);
        long modulo = MILLIS % UNIT.toMillis(1);

        if (modulo > 0) {
            String modstr = Long.toString(modulo);
            String mod = modstr.length() > 1 ? modstr.substring(0, 2) : modstr.substring(0, 1);
            if (mod.endsWith("0"))
                mod = mod.substring(0, 1);
            return "Duration: " + val + "." + mod + " " + UNIT.toString();
        }

        return "Duration: " + val + " " + UNIT.toString();
    }

}
