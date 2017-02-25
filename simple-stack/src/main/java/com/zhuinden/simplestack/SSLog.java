package com.zhuinden.simplestack;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to enable logging the state management of managed services.
 *
 * To use, add a logger with {@link SSLog#addLogger(SSLogger)}.
 */
public class SSLog {
    public interface SSLogger {
        void info(String tag, String message);
    }

    private SSLog() {
    }

    final static List<SSLogger> loggers = new ArrayList<>();

    /**
     * Checks if the SSLog has any loggers set.
     *
     * @return true if there are loggers.
     */
    public static boolean hasLoggers() {
        return !loggers.isEmpty();
    }

    /**
     * Adds a logger.
     * @param ssLogger    the logger to use
     */
    public static void addLogger(SSLogger ssLogger) {
        loggers.add(ssLogger);
    }

    /**
     * Removes the provided logger.
     * @param ssLogger the logger to remove
     */
    public static void removeLogger(SSLogger ssLogger) {
        loggers.remove(ssLogger);
    }

    /**
     * Removes all loggers.
     */
    public static void removeAllLoggers() {
        loggers.clear();
    }

    static void info(String tag, String message) {
        if(hasLoggers()) {
            for(SSLogger logger : loggers) {
                logger.info(tag, message);
            }
        }
    }
}
