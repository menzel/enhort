package de.thm.spring.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Path;

public class Settings {
    private static String contigsPath;
    private static String backendip;
    private static Path statpath;

    private static Logger logger = LoggerFactory.getLogger(StatisticsCollector.class);

    public static String getBackendip() {
        return backendip;
    }

    public static void setBackendip(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);

            if (!address.isReachable(1000))
                throw new Exception("Backend Server " + ip + " not reachable");

        } catch (Exception e) {
            logger.error("Exception {}", e.getMessage(), e);
        }

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
            logger.error("Exception {}", "Contigs path missing", "The contigs path " + contigsPath + " is missing");

        Settings.contigsPath = contigsPath;
    }
}

