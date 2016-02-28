package de.thm.run;

import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.spring.backend.StatisticsCollector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

/**
 * Created by Michael Menzel on 3/2/16.
 */
@SpringBootApplication
@ComponentScan(basePackages = "de.thm.spring")
@EnableAutoConfiguration
public class Server {

    private static List<Track> intervals;

    public static void main(String[] args) {
        TrackFactory loader = TrackFactory.getInstance();
        loader.loadIntervals();
        intervals = loader.getAllIntervals();

        attachShutDownHook();

        try {

            SpringApplication.run(Server.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            StatisticsCollector.getInstance().addErrorC();
        }
    }

    public static List<Track> getIntervals() {
        return intervals;
    }

    static void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                StatisticsCollector.getInstance().saveStats();
            }
        });
    }
}


