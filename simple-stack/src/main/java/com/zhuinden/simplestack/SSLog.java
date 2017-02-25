package com.zhuinden.simplestack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhuinden on 2017.02.25..
 */

public class SSLog {
    public interface SSLogger {
        void info(String tag, String message);
    }

    private SSLog() {
    }

    final static List<SSLogger> loggers = new ArrayList<>();

    public static boolean hasLoggers() {
        return !loggers.isEmpty();
    }

    public static void addLogger(SSLogger ssLogger) {
        loggers.add(ssLogger);
    }

    public static void removeLogger(SSLogger ssLogger) {
        loggers.remove(ssLogger);
    }

    public static void removeAllLoggers() {
        loggers.clear();
    }

    public static void info(String tag, String message) {
        if(hasLoggers()) {
            for(SSLogger logger : loggers) {
                logger.info(tag, message);
            }
        }
    }
}
