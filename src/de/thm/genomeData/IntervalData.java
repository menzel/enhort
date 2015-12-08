package de.thm.genomeData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class IntervalData extends Interval{


    /**
     *
     * @param file
     */
    public IntervalData(File file) {
        initMap();
        loadIntervalData(file);
    }

    /**
     *
     * @param file
     */
    private void loadIntervalData(File file){

        try(Stream<String> lines = Files.lines(file.toPath())){
            Iterator<String> it = lines.iterator();

            while(it.hasNext()){
                String line = it.next();
                String[] parts = line.split("\t");


                if(parts[1].matches("chr(\\d{1,2}|X|Y)")){ //TODO get other chromosoms

                    intervals.get(parts[1]).add(Long.parseLong(parts[3]));
                    intervals.get(parts[1]).add(Long.parseLong(parts[4]));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
