package com.wars.engine.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private Log() {
    }

    private static void log(LogLevel level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.printf("[%s] [%s] %s%n", timestamp, level, message);
    }

    public static void info(String message) {
        log(LogLevel.INFO, message);
    }

    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }

    private enum LogLevel {
        INFO,
        ERROR
    }

}
