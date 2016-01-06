package de.thm.genomeData;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public abstract class Interval implements Serializable{


    private static final long serialVersionUID = 1690225L;

    protected ArrayList<Long> intervalsStart = new ArrayList<>();
    protected ArrayList<Long> intervalsEnd = new ArrayList<>();

    protected ArrayList<String> intervalName = new ArrayList<>();
    protected ArrayList<Long> intervalScore = new ArrayList<>();


    protected Type type;

    public Type getType() {
        return type;
    }

    public enum Type {inout, score, named}


    /**
     *
     * @param file
     */
    protected void loadIntervalData(File file){

        try(Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)){
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

    public ArrayList<String> getIntervalName() {
        return intervalName;
    }

    public void setIntervalName(ArrayList<String> intervalName) {
        this.intervalName = intervalName;
    }

    public ArrayList<Long> getIntervalsStart() {
        return intervalsStart;
    }

    public void setIntervalsStart(ArrayList<Long> intervalsStart) {
        this.intervalsStart = intervalsStart;
    }

    public ArrayList<Long> getIntervalsEnd() {
        return intervalsEnd;
    }

    public void setIntervalsEnd(ArrayList<Long> intervalsEnd) {
        this.intervalsEnd = intervalsEnd;
    }

    public ArrayList<Long> getIntervalScore() {
        return intervalScore;
    }

    public void setIntervalScore(ArrayList<Long> intervalScore) {
        this.intervalScore = intervalScore;
    }

    public void setType(Type type) {
        this.type = type;
    }

    protected abstract void handleParts(String[] parts);


}
