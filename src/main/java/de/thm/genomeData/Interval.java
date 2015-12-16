package de.thm.genomeData;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public abstract class Interval implements Serializable{


    private static final long serialVersionUID = 1690225L;

    protected Map<String, ArrayList<Long>> intervalsStart = new HashMap<>();
    protected Map<String, ArrayList<Long>> intervalsEnd = new HashMap<>();

    protected Map<String, ArrayList<String>> intervalName = new HashMap<>();
    protected Map<String, ArrayList<Long>> intervalScore = new HashMap<>();

    protected Type type;

    public Type getType() {
        return type;
    }

    public enum Type {inout, score, named}


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
     */
    protected void initNameMap(Map<String, ArrayList<String>> map){

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

            lines.close();

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


    public Map<String, ArrayList<String>> getIntervalName() {
        return intervalName;
    }

    public Map<String, ArrayList<Long>> getIntervalScore() {
        return intervalScore;
    }

    public void setIntervalScore(Map<String, ArrayList<Long>> intervalScore) {
        this.intervalScore = intervalScore;
    }

    public void setIntervalName(Map<String, ArrayList<String>> intervalName) {
        this.intervalName = intervalName;
    }

    public void setIntervalsStart(Map<String, ArrayList<Long>> intervalsStart) {
        this.intervalsStart = intervalsStart;
    }

    public void setIntervalsEnd(Map<String, ArrayList<Long>> intervalsEnd) {
        this.intervalsEnd = intervalsEnd;
    }

    public void setType(Type type) {
        this.type = type;
    }

    protected abstract void handleParts(String[] parts);


}
