package com.botiss;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ConsoleInterceptor {

    public interface Block {
        void call() throws Exception;
    }

    public static String copyOut(Block block) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(bos, true);
        PrintStream oldStream = System.out;
        System.setOut(printStream);
        try {
            block.call();
        } finally {
            System.setOut(oldStream);
        }
        return bos.toString();
    }
}