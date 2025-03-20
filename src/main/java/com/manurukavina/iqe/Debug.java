package com.manurukavina.iqe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Provides logging functionality for debugging purposes, allowing messages to be
 * logged at different levels (INFO, WARN, ERROR) if debug mode is enabled.
 */
public class Debug {
    private final Runtime runtime;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Enum representing different logging levels with corresponding prefixes.
     */
    public enum Level {
        INFO("[INFO]"),
        WARN("[WARN]"),
        ERROR("[ERROR]");

        private final String prefix;

        /**
         * Constructs a logging level with a specified prefix.
         *
         * @param prefix The prefix to be used in log messages.
         */
        Level(String prefix) {
            this.prefix = prefix;
        }

        /**
         * Retrieves the prefix associated with the logging level.
         *
         * @return The prefix string.
         */
        public String getPrefix() {
            return prefix;
        }
    }

    /**
     * Constructs a Debug instance with a given runtime configuration.
     *
     * @param runtime The runtime instance containing debug mode settings.
     */
    public Debug(Runtime runtime) {
        this.runtime = runtime;
    }

    /**
     * Logs a message at the specified level if debug mode is enabled.
     *
     * @param level   The log level (INFO, WARN, ERROR).
     * @param message The message to log.
     */
    private void write(Level level, String message) {
        if (runtime.isDebugMode()) {
            String timestamp = LocalDateTime.now().format(formatter);
            System.out.println(String.format("%s %s %s", 
                timestamp, 
                level.getPrefix(), 
                message));
        }
    }

    /**
     * Logs an informational message if debug mode is enabled.
     *
     * @param message The message to log.
     */
    public void info(String message) {
        write(Level.INFO, message);
    }

    /**
     * Logs a warning message if debug mode is enabled.
     *
     * @param message The warning message to log.
     */
    public void warn(String message) {
        write(Level.WARN, message);
    }

    /**
     * Logs an error message if debug mode is enabled.
     *
     * @param message The error message to log.
     */
    public void error(String message) {
        write(Level.ERROR, message);
    }
}
