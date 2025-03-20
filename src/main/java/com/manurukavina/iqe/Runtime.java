package com.manurukavina.iqe;

import org.apache.commons.cli.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The Runtime class handles configuration parameters for the application,
 * including command-line argument parsing and setting runtime options.
 */
@Getter
@Setter
public class Runtime {
    private String version = "1.0.0";
    private int port = 8641;
    private int backlog = 0;
    private int threadPoolSize = 0;
    private boolean debugMode = false;
    private boolean getAllowed = true;
    private Debug debugger;

    /**
     * Parses command-line arguments to configure the runtime settings.
     *
     * @param args Command-line arguments.
     */
    public void create(String[] args) {
        setVersion(version);
        debugger = new Debug(this);

        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("p")) {
                int newPort = Integer.parseInt(cmd.getOptionValue("p"));
                if (newPort < 0 || newPort > 65535) {
                    throw new NumberFormatException("Port must be between 0 and 65535");
                }
                setPort(newPort);
            }

            if (cmd.hasOption("b")) {
                int newBacklog = Integer.parseInt(cmd.getOptionValue("b"));
                if (newBacklog < 0) {
                    newBacklog = 0;
                }
                setBacklog(newBacklog);
            }

            if (cmd.hasOption("t")) {
                int newThreadPoolSize = Integer.parseInt(cmd.getOptionValue("t"));
                if (newThreadPoolSize < 0) {
                    throw new NumberFormatException("Thread pool size must be non-negative");
                }
                setThreadPoolSize(newThreadPoolSize);
            }

            if (cmd.hasOption("d")) {
                setDebugMode(true);
            }
            if (cmd.hasOption("ng")) {
                setGetAllowed(false);
            }

            if (isDebugMode()) {
                printConfiguration();
            }

        } catch (ParseException | NumberFormatException e) {
            System.out.println("Invalid arguments: " + e.getMessage());
            debugger.error("Failed to parse arguments: " + e.getMessage());
            printHelp(options);
        }
    }

    /**
     * Creates command-line options for configuring the application.
     *
     * @return A set of command-line options.
     */
    private Options createOptions() {
        Options options = new Options();
        options.addOption("p", "port", true, "Port number to run the server on.");
        options.addOption("b", "backlog", true, "Maximum number of pending connections.");
        options.addOption("t", "threads", true, "Number of threads in the thread pool.");
        options.addOption("d", "debug", false, "Enable debug mode.");
        options.addOption("ng", "noget", false, "Disable GET App.");
        return options;
    }

    /**
     * Prints the current runtime configuration when debug mode is enabled.
     */
    private void printConfiguration() {
        debugger.info("Configuration:");
        debugger.info("Version: " + getVersion());
        debugger.info("Port: " + getPort());
        debugger.info("Backlog: " + getBacklog());
        debugger.info("Thread Pool Size: " + getThreadPoolSize());
        debugger.info("Debug Mode: " + isDebugMode());
        debugger.info("GET App active: " + isGetAllowed());
    }

    /**
     * Prints help information about the available command-line options.
     *
     * @param options The command-line options to display in the help message.
     */
    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar iqe-" + getVersion() + ".jar\n\n", options, true);
        System.exit(0);
    }
}
