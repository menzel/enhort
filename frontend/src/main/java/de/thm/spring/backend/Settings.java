package de.thm.spring.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

public class Settings {
    private static String contigsPath;
    private static String backendip;
    private static Path statpath;
    private static int port;

    private static Logger logger = LoggerFactory.getLogger(StatisticsCollector.class);

    public static String getBackendip() {
        return backendip;
    }

    public static void setBackendip(String ip) {

        Settings.backendip = ip;
    }

    public static Path getStatfile_path() {
        return statpath;
    }

    public static void setStatfile_path(String logfile_path) {
        Settings.statpath = new File(logfile_path).toPath().toAbsolutePath();
    }

    public static String getContigsPath() {
        return contigsPath;
    }

    public static void setContigsPath(String contigsPath) {
        if (!new File(contigsPath).exists())
            logger.error("Exception {}{}", "Contigs path missing", "The contigs path " + contigsPath + " is missing");

        Settings.contigsPath = contigsPath;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(Integer p) {
        port = p;
    }
}

