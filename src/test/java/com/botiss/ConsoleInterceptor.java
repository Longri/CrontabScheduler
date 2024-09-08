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
package com.botiss;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ConsoleInterceptor {

    public interface Block {
        void call() throws Exception;
    }

    public static String copyOut(Block block) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(bos);
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