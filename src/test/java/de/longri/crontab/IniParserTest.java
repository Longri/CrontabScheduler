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
import org.ini4j.Ini;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IniParserTest {

    @Test
    void parseJobs() throws Exception {
//        if (EXCLUDE.ex()) return;
        URL resourceURL = IniParserTest.class.getResource("/test.ini");
        File resourceFile = new File(resourceURL.getFile());
        InputStream inputStream = IniParserTest.class.getResourceAsStream("/test.ini");

        Ini ini = new Ini();
        ini.load(inputStream);

        Ini.Section jobs_section = ini.get("jobs");
        int count = Integer.parseInt(jobs_section.get("count"));
        assertEquals(1, count);

        String job1 = jobs_section.get("job1");
        assertEquals("0 0/5 * ? * * *; LOGGING ;JobName1;Log test", job1);

        List<Cronjob> jobs = new CronJobList(resourceFile);
        assertEquals(1, jobs.size());

        Job_Logging job_logging = (Job_Logging) jobs.get(0).job;
        assertEquals("Log test", job_logging.MSG);

        LocalDateTime ldt = LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
        assertTrue(jobs.get(0).isRunNextMin(5));

    }

    @Test
    void testCronJobList() throws Exception {


        //create iniFile

        File fileHandle = new File("./testIniFile.ini");
        if (fileHandle.exists()) assertTrue(fileHandle.delete(), "test file must deleted");

        //create List
        CronJobList list = new CronJobList(fileHandle);

        assertNotNull(list);
        assertEquals(0, list.size());

        // create Job
        Job_Logging job = new Job_Logging("JobName", "JobMsg");
        assertEquals("JobName", job.getName());
        assertEquals("JobMsg", job.getArgs());
        assertEquals(JobType.getFromString("LOGGING"), job.getType());

        //create CronJob
        Cronjob cron = new Cronjob("0 0/5 * ? * * *", job); //alle 5min
        assertEquals("JobName", cron.getName());
        assertEquals("0 0/5 * ? * * *; LOGGING ;JobName ;JobMsg", cron.toIniString());

        //add CronJob to List
        list.add(cron);
        assertEquals(1, list.size());

        //write Ini File
        list.write();
        assertTrue(fileHandle.exists());
//        assertEquals(70, fileHandle.length());
        String expected = "[jobs]\n" +
                "count = 1\n" +
                "job1 = 0 0/5 * ? * * *; LOGGING ;JobName ;JobMsg\n" +
                "\n";
        assertEquals(expected, Files.readString(fileHandle.toPath()));

        //Read ini file into new Object
        CronJobList newList = new CronJobList(fileHandle);
        assertNotNull(newList);
        assertEquals(1, newList.size());

        Cronjob newCron = newList.get(0);
        assertEquals("JobName", newCron.getName());
        assertEquals("0 0/5 * ? * * *; LOGGING ;JobName ;JobMsg", newCron.toIniString());

        Job newJob = newCron.job;
        assertEquals("JobName", newJob.getName());
        assertEquals("JobMsg", newJob.getArgs());
        assertEquals(JobType.getFromString("LOGGING"), newJob.getType());

    }

    @Test
    void loadIniFile() throws Exception {
        File fileHandle = new File("./TestFolder/TEST/testIniFile.ini");
        assertTrue(fileHandle.exists(), "test file must exist");
        CronJobList jobList = new CronJobList(fileHandle);
        assertEquals(1, jobList.size(), "size must be 1");
        Cronjob cron = jobList.get(0);
        assertEquals("JobName1", cron.getName());
        assertEquals("0 0/5 * ? * * *; LOGGING ;JobName1 ;Log test", cron.toIniString());
        assertInstanceOf(Job_Logging.class, cron.job);

    }


    @Test
    void parseCopyJobs() throws URISyntaxException, IOException, ParseException, org.apache.commons.cli.ParseException {

//        assertTrue(false); //TODO implement copy test

//        if (EXCLUDE.ex()) return;
//        File inifile = new File(getClass().getClassLoader().getResource("test.ini").toURI());
//        Ini ini = new Ini();
//        ini.load(new FileReader(inifile));
//
//        Ini.Section jobs_section = ini.get("jobs");
//        int count = Integer.parseInt(jobs_section.get("count"));
//        assertEquals(1, count);
//
//        String job1 = jobs_section.get("job1");
//        assertEquals("0 0/1 * ? * * *: LOGGING :Log test", job1);
//
//        List<Cronjob> jobs = IniParser.parseJobs(inifile);
//        assertEquals(1, jobs.size());
//
//        Job_Logging job_logging = (Job_Logging) jobs.get(0).job;
//        assertEquals("Log test", job_logging.MSG);
//
//        LocalDateTime ldt = LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
//        assertTrue(jobs.get(0).isRunNextMin(1));
//
//        jobs.get(0).waitAndRun(1);
//        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
//        assertEquals(ldt, now);

    }


}