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

    protected Map<String, ArrayList<Long>> intervalsStart = new HashMap<>();
    protected Map<String, ArrayList<Long>> intervalsEnd = new HashMap<>();
    private int positionCount;


    /**
     *
     */
    protected void initMap(Map<String, ArrayList<Long>> map){

        for(int i = 1; i <= 22; i++){
            map.put("chr"+i, new ArrayList<>());
        }

        map.put("chrX", new ArrayList<>());
        map.put("chrY", new ArrayList<>());

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

                handleParts(parts);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Map<String, ArrayList<Long>> getIntervalStarts() {
        return intervalsStart;
    }

    public Map<String, ArrayList<Long>> getIntervalsEnd() {
        return intervalsEnd;
    }


    protected abstract void handleParts(String[] parts);


}
