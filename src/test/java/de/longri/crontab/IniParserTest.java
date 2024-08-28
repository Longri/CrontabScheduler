package de.longri.crontab;

import de.longri.crontab.type.JobType;
import de.longri.filetransfer.FileTransferHandle;
import de.longri.filetransfer.Local_FileTransferHandle;
import de.longri.filetransfer.ResourceTransferHandler;
import de.longri.fx.TRANSLATION;
import de.longri.utils.SystemType;
import org.ini4j.Ini;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IniParserTest {

    @Test
    void parseJobs() throws Exception {
//        if (EXCLUDE.ex()) return;
        FileTransferHandle iniFileHandle = new ResourceTransferHandler("./test.ini");
        Ini ini = new Ini();
        ini.load(iniFileHandle.read());

        Ini.Section jobs_section = ini.get("jobs");
        int count = Integer.parseInt(jobs_section.get("count"));
        assertEquals(1, count);

        String job1 = jobs_section.get("job1");
        assertEquals("0 0/5 * ? * * *; LOGGING ;JobName1;Log test", job1);

        List<Cronjob> jobs = new CronJobList(iniFileHandle);
        assertEquals(1, jobs.size());

        Job_Logging job_logging = (Job_Logging) jobs.get(0).job;
        assertEquals("Log test", job_logging.MSG);

        LocalDateTime ldt = LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
        assertTrue(jobs.get(0).isRunNextMin(5));

    }

    @Test
    void testCronJobList() throws Exception {
        TRANSLATION.INITIAL("uiText");

        //create iniFile
        FileTransferHandle fileHandle = new Local_FileTransferHandle("./testIniFile.ini");
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
        assertEquals(expected, fileHandle.readString());

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
        FileTransferHandle fileHandle = new Local_FileTransferHandle("./TestFolder/TEST/testIniFile.ini");
        assertTrue(fileHandle.exists(), "test file must exist");
        CronJobList jobList = new CronJobList(fileHandle);
        assertEquals(1, jobList.size, "size must be 1");
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