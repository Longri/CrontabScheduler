package de.longri.crontab;

import de.longri.crontab.type.JobType;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class Job_CopyTest {

    static ArrayList<String> clearList = new ArrayList<>();


    @AfterAll
    static void afterAll() {
        //clean up
        File targetFile = new File("./TEST/changeCopy.txt");
        if (targetFile.exists()) {
            targetFile.delete();
        }

        for (String path : clearList) {
            File targetZipFile = new File(path);
            if (targetZipFile.exists()) {
                targetZipFile.delete();
            }
        }
    }

    @Test
    void constructorTest() throws ParseException {
        assertThrows(MissingOptionException.class, () -> new Job_Copy("TestName", "-s test -z"));

        Job_Copy jc = new Job_Copy("TestName", "-s sourcePath -target targetPath");

        assertEquals("sourcePath", jc.source);
        assertEquals("targetPath", jc.target);
        assertEquals(false, jc.zip);

        jc = new Job_Copy("TestName", "-source sourcePath -t targetPath -z");

        assertEquals("sourcePath", jc.source);
        assertEquals("targetPath", jc.target);
        assertEquals(true, jc.zip);

    }

    @Test
    void copyTest() throws Exception {
        Job_Copy jc = new Job_Copy("TestName", "-s ./change.txt -target ./TestFolder/TEST/changeCopy.txt");

        jc.run();

        File sourceFile = new File("./change.txt");
        File targetFile = new File("./TestFolder/TEST/changeCopy.txt");

        assertTrue(targetFile.exists());
        assertEquals(Files.readAllLines(sourceFile.toPath()), Files.readAllLines(targetFile.toPath()));
    }

    @Test
    void copyTest_no_source() throws ParseException {
        Job_Copy jc = new Job_Copy("TestName", "-s ./no_change.txt -target ./TestFolder/TEST/changeCopy2.txt");
        boolean thrown = true;
        try {
            jc.run();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof FileNotFoundException)
                thrown = false;
        }
        assertFalse(thrown, "method must thrown a FileNotFoundException");
    }


    @Test
    void zipTest() throws Exception {
        Job_Copy jc = new Job_Copy("TestName", "-s ./src -target ./TestFolder/TEST/source.zip -z");

        jc.run();

        File targetFile = new File("./TestFolder/TEST/source.zip");

        assertTrue(targetFile.exists());

        File extractFolder = new File("./TestFolder/TEST/");
        File extractSrcFolder = new File("./TestFolder/TEST/src");
        File expected = new File("./src");

        unzip(targetFile, extractFolder);
        assertTrue(compareFolders(expected, extractSrcFolder));

        targetFile.delete();
        deleteDirectoryRecursion(extractSrcFolder);
    }

    public static void unzip(File archive, File destDir) {

        String targetZipFilePath = archive.getAbsolutePath();
        String destinationFolderPath = destDir.getAbsolutePath();
        String password = "";

        try {
            ZipFile zipFile = new ZipFile(targetZipFilePath);
            zipFile.extractAll(destinationFolderPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean compareFolders(File dir1, File dir2) throws IOException {
        final Path pathOne = dir1.toPath();
        final Path pathSecond = dir2.toPath();


        // get content of first directory
        final TreeSet<String> treeOne = new TreeSet();
        Files.walkFileTree(pathOne, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relPath = pathOne.relativize(file);
                String entry = relPath.toString();
                treeOne.add(entry);
                return FileVisitResult.CONTINUE;
            }
        });

        // get content of second directory
        final TreeSet<String> treeSecond = new TreeSet();
        Files.walkFileTree(pathSecond, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relPath = pathSecond.relativize(file);
                String entry = relPath.toString();
                treeSecond.add(entry);
                return FileVisitResult.CONTINUE;
            }
        });
        assertEquals(treeOne, treeSecond);
        return treeOne.equals(treeSecond);
    }

    void deleteDirectoryRecursion(File file) throws IOException {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }

    @Test
    void zipTestDatePattern() throws Exception {

        String datePattern = DateTimeFormatter.ofPattern("yyyy MM dd").format(LocalDateTime.now());


        Job_Copy jc = new Job_Copy("TestName", "-s ./src -target \"./TestFolder/TEST/source.zip\" -z -dp \"yyyy MM dd\"");

        jc.run();

        String path = "./TestFolder/TEST/source_" + datePattern + ".zip";
        File targetFile = new File(path);

        assertTrue(targetFile.exists());
        clearList.add(path);
    }


    final Job_Copy DUMMY;
    final String ARGS = "-s ./src -target ./TestFolder/TEST/source.zip -z -dp \"yyyy MM dd\"";

    {
        try {
            DUMMY = new Job_Copy("DummyJobName", ARGS);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getType() {
        assertEquals(JobType.getFromString("COPY"), DUMMY.getType());
    }

    @Test
    void getSetName() {
        String oldName = DUMMY.getName();
        assertEquals("DummyJobName", DUMMY.getName());
        DUMMY.setName("newName");
        assertEquals("newName", DUMMY.getName());
        DUMMY.setName(oldName);
    }

    @Test
    void getSetArgs() {
        assertEquals(ARGS, DUMMY.getArgs());
        String newArgs = "-s ./newsrc -target ./TestFolder/TEST/target -dp \"dd MM yyyy\"";
        DUMMY.setArgs(newArgs);
        assertEquals(newArgs, DUMMY.getArgs());
        assertEquals("./newsrc", DUMMY.source);
        assertEquals("./TestFolder/TEST/target", DUMMY.target);
        assertEquals("dd MM yyyy", DUMMY.dateTimeFormatString);
        DUMMY.setArgs(ARGS);
    }


}