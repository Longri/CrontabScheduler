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


import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class CronJobList extends ArrayList<Cronjob> {

    private final File FILE;
    private final Ini ini;

    public CronJobList(File iniFile) throws Exception {
        this.FILE = iniFile;
        ini = new Ini();
        if (iniFile.exists()) {
            ini.load(FILE);
            readIni();
        }
    }

    private void readIni() throws Exception {
        int count = Integer.parseInt(ini.get("jobs", "count"));
        for (int i = 1; i < count + 1; i++) { //index starts with 1
            this.add(Cronjob.getFromIniString(ini.get("jobs", "job" + i)));
        }
    }

    public void write() throws IOException {

        ini.clear();

        ini.add("jobs", "count", this.size());
        for (int i = 0; i < this.size(); i++) {
            ini.add("jobs", "job" + (i + 1), this.get(i).toIniString());//index starts with 1
        }
        ini.store(FILE);
    }

}
