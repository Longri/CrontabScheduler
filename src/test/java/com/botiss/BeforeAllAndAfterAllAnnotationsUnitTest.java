package com.botiss;
import de.longri.crontab.Main;
import de.longri.logging.LongriLoggerConfiguration;
import de.longri.logging.LongriLoggerFactory;
import de.longri.utils.SystemType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeforeAllAndAfterAllAnnotationsUnitTest {

    static Logger log;

    @BeforeAll
    public static void setup() {

        //initial Logger
        try {
            if (SystemType.getSystemType() == SystemType.WIN) {
                LongriLoggerConfiguration.setConfigurationFile(Main.class.getClassLoader().getResourceAsStream("logger/LongriLogger-win.properties"));
            } else {
                LongriLoggerConfiguration.setConfigurationFile(Main.class.getClassLoader().getResourceAsStream("logger/LongriLogger.properties"));
            }
            ((LongriLoggerFactory) LoggerFactory.getILoggerFactory()).reset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        log = LoggerFactory.getLogger(BeforeAllAndAfterAllAnnotationsUnitTest.class);
        if (log != null) log.debug("Setup JUnit Tests");
    }


    @AfterAll
    public static void tearDown() {
        if (log != null) log.debug("tearDown JUnit Tests");
    }

}