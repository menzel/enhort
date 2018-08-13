package de.thm.spring.run;

import de.thm.spring.backend.StatisticsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Server
 *
 * Created by Michael Menzel on 3/2/16.
 */
@SpringBootApplication
@ComponentScan(basePackages = "de.thm.spring")
public class Webinterface {

    private static final Logger logger = LoggerFactory.getLogger(Webinterface.class);

    public static void main(String[] args) {


        try {
            SpringApplication.run(Webinterface.class, args);
            attachShutDownHook();

        } catch (Exception e) {
            logger.error("Exception {}", e.getMessage(), e);
            StatisticsCollector.getInstance().addErrorC();
        }
    }

    /**
     * Shutdown hook to save the stats before exit
     */
    private static void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> StatisticsCollector.getInstance().saveStats()));
    }
}
