package de.thm.run;

import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalLoader;
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

   private static IntervalLoader loader;
   private static Map<String, Interval> intervals;

   public static void main(String[] args){
      loader = IntervalLoader.getInstance();
      intervals = loader.getAllIntervals();

      SpringApplication.run(Server.class, args);
   }

   public static Map<String, Interval> getIntervals() {
      return intervals;
   }
}
