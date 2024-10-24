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


import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {
    static final int WAIT_TIME = 10;


    private static Logger log = LoggerFactory.getLogger(Main.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd hh:mm");

    public static void main(String... args) throws IOException, java.text.ParseException, InterruptedException, ParseException, GeneralSecurityException {
        try {

            CommandLine cmd = getCMD(args);

            if (cmd.hasOption('l')) {
                // inital logger
                String logger_ini_path = cmd.getOptionValue('l').trim();

                Properties ini = new Properties();
                ini.load(Files.newBufferedReader(new File(logger_ini_path).toPath()));

                // write propperties to System

                for (String key : ini.stringPropertyNames()) {
                    System.setProperty(key, ini.getProperty(key));
                }

            }

            log = LoggerFactory.getLogger(Main.class);

            log.info("Start with ARGS: {}", Arrays.toString(args));

            log.debug("run with ini file: {}", cmd.getOptionValue("p"));
            File fileHandle = new File(cmd.getOptionValue("p").trim());
            CronJobList jobs = new CronJobList(fileHandle);
            log = LoggerFactory.getLogger(Main.class);
            log.debug("Found {} jobs in Inifile", jobs.size());

            AtomicInteger runningJobs = new AtomicInteger(0);
            int idx = 0;
            for (Cronjob job : jobs) {
                idx++;
                if (job.isRunNextMin(WAIT_TIME)) {
                    int count = job.executionCountNextMinute(WAIT_TIME);
                    log.debug("Job {} runs {} times in the next {} minutes", "Job" + idx, count, WAIT_TIME);
                    runningJobs.incrementAndGet();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            job.waitAndRun(WAIT_TIME);
                            runningJobs.decrementAndGet();
                        }
                    }).start();
                } else {
                    String nextExe = DATE_TIME_FORMATTER.format(job.cronExpression.nextTimeAfter(LocalDateTime.now()));
                    log.debug("Job {} dont run in the next {} minutes! Next execution is: {}", "Job" + idx, WAIT_TIME, nextExe);
                }
            }

            int ruJo = runningJobs.get();
            if (ruJo > 0)
                log.debug("waiting for " + ruJo + " jobs");

            //wait for all running jobs are ready
            while (runningJobs.get() > 0) {
                Thread.sleep(250);
            }
            log.debug("End of execution \n------------------------------------------------------------");
            System.out.println("End of execution");
        } catch (Exception e) {
            log.error("ERROR at execution:", e);
            System.out.println("Error at execution: ");
            e.printStackTrace();
        }
    }

    private static CommandLine getCMD(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            return parser.parse(getOptions(), args);
        } catch (ParseException e) {
            log.error(e.getMessage());
            formatter.printHelp("Java Crontab Scheduler", getOptions());
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter pw = new PrintWriter(stringWriter);
            formatter.printHelp(pw, formatter.getWidth(), "Java Crontab Scheduler", null, getOptions(),
                    formatter.getLeftPadding(), formatter.getDescPadding(), null, false);
            log.debug(stringWriter.toString());
            log.debug("End of execution \n------------------------------------------------------------");
            System.exit(1);
        }
        return null;
    }

    private static Options getOptions() {
        Options options = new Options();

        Option host = new Option("p", "ini-path", true, "Path to ini file");
        host.setRequired(true);
        options.addOption(host);

        Option logFile = new Option("l", "log-ini", true, "Path to log ini file");
        logFile.setRequired(false);
        options.addOption(logFile);


        return options;
    }

}
