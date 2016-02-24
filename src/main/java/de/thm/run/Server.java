package de.thm.run;

import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalFactory;
import de.thm.spring.backend.StatisticsCollector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Map;

/**
 * Created by Michael Menzel on 3/2/16.
 */
@SpringBootApplication
@ComponentScan(basePackages = "de.thm.spring")
@EnableAutoConfiguration
public class Server {

   private static IntervalFactory loader;
   private static Map<String, Interval> intervals;

   public static void main(String[] args){
      loader = IntervalFactory.getInstance();
      intervals = loader.getAllIntervals();

      attachShutDownHook();
       try{

           SpringApplication.run(Server.class, args);
       } catch (Exception e){
           e.printStackTrace();
           StatisticsCollector.getInstance().addErrorC();
       }
   }

   public static Map<String, Interval> getIntervals() {
      return intervals;
   }

    public static void attachShutDownHook(){
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                StatisticsCollector.getInstance().saveStats();
            }
        });
    }
}


