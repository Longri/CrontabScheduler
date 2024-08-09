/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of fxutils.
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
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Job_Copy extends Job {

     boolean zip;
     String source;
     String target;
    protected DateTimeFormatter dateTimeFormatter;

    protected String dateTimeFormatString;


    public Job_Copy(String name, String args) throws ParseException {
        super(name);
        CommandLine cmd = getCMD(args);
        zip = cmd.hasOption("z");
        source = cmd.getOptionValue("source").trim();
        target = cmd.getOptionValue("target").trim();
        dateTimeFormatString = cmd.getOptionValue("date-pattern");
        if (dateTimeFormatString != null) {
            dateTimeFormatString = dateTimeFormatString.trim();
            dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormatString);
        } else {
            dateTimeFormatter = null;
        }
    }


    public static CommandLine getCMD(String args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            return parser.parse(getOptions(), getArgs(args));
        } catch (ParseException e) {
            throw e;
        }
    }

    private static Options getOptions() {
        Options options = new Options();

        Option source = new Option("s", "source", true, "Path to source File");
        source.setRequired(true);
        options.addOption(source);

        Option target = new Option("t", "target", true, "Path to target File");
        target.setRequired(true);
        options.addOption(target);

        Option zip = new Option("z", "zip", false, "Flag for zip source");
        zip.setRequired(false);
        options.addOption(zip);

        Option datePattern = new Option("dp", "date-pattern", true, "Date naming pattern");
        datePattern.setRequired(false);
        options.addOption(datePattern);

        return options;
    }


    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void work() throws RuntimeException {

        String targetPath = this.target;

        if (dateTimeFormatter != null) {
            String formatDate = dateTimeFormatter.format(LocalDateTime.now());
            int pos = target.lastIndexOf(".");
            String ext = target.substring(pos);
            String path = target.substring(0, pos);
            targetPath = path + "_" + formatDate.trim() + ext;
        }

        try {
            if (zip) {
                FileOutputStream fos = new FileOutputStream(targetPath);
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                File fileToZip = new File(source);
                zipFile(fileToZip, fileToZip.getName(), zipOut);
                zipOut.close();
                fos.close();
            } else {
                FileUtils.copyFile(new File(source), new File(targetPath));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public JobType getType() {
        return JobType.getFromString("COPY");
    }


    @Override
    public String getArgs() {
        StringBuilder sb = new StringBuilder();
        sb.append("-s ").append(source).append(" ");
        sb.append("-target ").append(target).append(" ");
        if (zip) sb.append("-z ");
        if (dateTimeFormatString != null) sb.append("-dp \"").append(dateTimeFormatString).append("\"");
        return sb.toString();
    }

    @Override
    public void setArgs(String args) {
        CommandLine cmd = null;
        try {
            cmd = getCMD(args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        zip = cmd.hasOption("z");
        source = cmd.getOptionValue("source").trim();
        target = cmd.getOptionValue("target").trim();
        dateTimeFormatString = cmd.getOptionValue("date-pattern").trim();
        if (dateTimeFormatString != null) {
            dateTimeFormatString = dateTimeFormatString.trim();
            dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormatString);
        } else {
            dateTimeFormatter = null;
        }
    }


    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (false && fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
