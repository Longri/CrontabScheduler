package com.botiss.prtg.backup.crontab;

import com.botiss.ConsoleInterceptor;
import de.longri.crontab.Main;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

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
        if (EXCLUDE.ex()||true) return;
        // write a ini file
        String iniContent = "[jobs]\n" +
                "count=2\n" +
                "job1=0 0/1 * ? * * *: LOGGING :Log test every minute for 10 minutes\n" +
                "job2=0 0 23 ? * * *: LOGGING :Log test every day at 23:00 ";

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

        assertTrue(lines[0].contains("Start with ARGS: [-p ./iniFile.ini]"));
        assertTrue(lines[1].contains("run with ini file:  ./iniFile.ini"));
        assertTrue(lines[2].contains("Found 2 jobs in Inifile"));
        assertTrue(lines[3].contains("Job Job1 runs 10 times in the next 10 minutes"));
        assertTrue(lines[4].contains("Job Job2 dont run in the next 10 minutes"));
        assertTrue(lines[25].contains("End of execution"));
        assertEquals(10, StringUtils.countMatches(result, "RUN => Last"));


    }
}