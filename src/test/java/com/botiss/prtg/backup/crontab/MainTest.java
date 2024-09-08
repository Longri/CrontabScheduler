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
package com.botiss.prtg.backup.crontab;

import com.botiss.ConsoleInterceptor;
import de.longri.crontab.Main;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @BeforeAll
    static void beforeAll() {
        // initial Logger
        // Set the log level to DEBUG
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
        System.setProperty("org.slf4j.simpleLogger.log.org.reflections.Reflections", "warn");
    }


    @AfterAll
    static void afterAll() {
        String path = "./iniFile.ini";
        File file = new File(path);
        // If file doesn't exists, then create it
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void main() throws Exception {
        if (EXCLUDE.LongTimeTests()) return;

        // write a ini file
        String iniContent = "[jobs]\n" +
                "count=2\n" +
                "job1=0 0/1 * ? * * *; LOGGING ;NameOf Logging job1;Log test every minute for 10 minutes\n" +
                "job2=0 0 23 ? * * *; LOGGING ;NameOf Logging job2;Log test every day at 23:00 ";

        String path = "./iniFile.ini";

        File file = new File(path);

        // If file doesn't exists, then create it
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(iniContent);
        writer.close();

        String result = ConsoleInterceptor.copyOut(() -> {
            // start main with ini file as arg
            Main.main(new String[]{"-p ./iniFile.ini"});
        });

        String[] lines = result.split("\n");

        //print lines for debug
        int i = 0;
        for (String line : lines) {
            System.out.println("[" + i++ + "] " + line);
        }

        assertTrue(lines[0].contains("Start with ARGS: [-p ./iniFile.ini]"));
        assertTrue(lines[1].contains("run with ini file:  ./iniFile.ini"));
        assertTrue(lines[2].contains("Found 2 jobs in Inifile"));
        assertTrue(lines[3].contains("Job Job1 runs 10 times in the next 10 minutes"));
        assertTrue(lines[4].contains("Job Job2 dont run in the next 10 minutes"));
        assertTrue(lines[98].contains("End of execution"));
        assertEquals(10, StringUtils.countMatches(result, "RUN => Last"));


    }
}