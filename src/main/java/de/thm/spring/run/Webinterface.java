package de.thm.spring.run;

import de.thm.spring.backend.BackendConnector;
import de.thm.spring.backend.StatisticsCollector;
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
@EnableAutoConfiguration
public class Webinterface {

    public static void main(String[] args) {


        try {
            BackendConnector connector = BackendConnector.getInstance();
            new Thread(connector).run();

            SpringApplication.run(Webinterface.class, args);

            attachShutDownHook();

        } catch (Exception e) {
            e.printStackTrace();
            StatisticsCollector.getInstance().addErrorC();
        }
    }

    /**
     * Shutdown hook to save the stats before exit
     */
    static void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                StatisticsCollector.getInstance().saveStats();
            }
        });
    }

}
