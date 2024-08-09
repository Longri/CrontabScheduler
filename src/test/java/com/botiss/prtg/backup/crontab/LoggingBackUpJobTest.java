package com.botiss.prtg.backup.crontab;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggingBackUpJobTest {


    DateTimeFormatter logFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void TestLogging() throws IOException {
        File pathToLogFolder = new File("./TestFolder/prtg-backup/logs/");
        final int expectedFileCount = 5;
        int fileCount = 0;
        File[] files = pathToLogFolder.listFiles();

        for (File file : files) {
            if (file.exists() && !file.isDirectory() && file.getAbsolutePath().endsWith(".log.gz")) {
                System.out.println("Check File: " + file.getName());
                LocalDateTime expectedRunTime = LocalDateTime.parse("2022-01-01 00:00:00", logFormatter);
                List<String> runLines = readGzipLines(file);
                int runCount = 0;
                for (String line : runLines) {
                    String logTimeString = line.substring(0, 20).trim();
                    LocalDateTime logRunTime = LocalDateTime.parse(logTimeString, logFormatter);

                    // hour minute must be equals
                    assertEquals(expectedRunTime.getHour(), logRunTime.getHour());
                    assertEquals(expectedRunTime.getMinute(), logRunTime.getMinute());
                    expectedRunTime = expectedRunTime.plusMinutes(5);
                    runCount++;
                }
                assertEquals(24 * 12, runCount);
                fileCount++;
            }
        }
        assertEquals(expectedFileCount, fileCount);
    }


    List<String> readGzipLines(File file) throws IOException {
        ObservableList<String> linesWithRun = FXCollections.observableArrayList();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(new FileInputStream(file))));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.contains("- RUN =>")) {
                linesWithRun.add(line);
            }
        }
        return linesWithRun;
    }
}
