package de.thm.genomeData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public abstract class Interval {

    protected Map<String, ArrayList<Long>> intervals = new HashMap<>();
    private int positionCount;


    /**
     *
     */
    protected void initMap(){

        for(int i = 1; i <= 22; i++){
            intervals.put("chr"+i, new ArrayList<>());
        }

        intervals.put("chrX", new ArrayList<>());
        intervals.put("chrY", new ArrayList<>());

    }
    /**
     *
     * @param file
     */
    protected void loadIntervalData(File file){

        try(Stream<String> lines = Files.lines(file.toPath())){
            Iterator<String> it = lines.iterator();

            while(it.hasNext()){
                String line = it.next();
                String[] parts = line.split("\t");


                if(parts[1].matches("chr(\\d{1,2}|X|Y)")){ //TODO get other chromosoms

                    handleParts(parts);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected abstract void handleParts(String[] parts);


    public Map<String, ArrayList<Long>> getIntervals() {
        return intervals;
    }
}
