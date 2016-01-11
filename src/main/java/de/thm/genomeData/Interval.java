package de.thm.genomeData;

import de.thm.calc.PositionPreprocessor;
import de.thm.misc.ChromosomSizes;

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
public class Interval implements Serializable{


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

    public Interval(){}
     /**
     *
     * @param file
     */
    public Interval(File file, Type type) {

        this.type = type;

        loadIntervalData(file);

        if(type == Type.inout)
            PositionPreprocessor.preprocessData(intervalsStart,intervalsEnd,intervalName, intervalScore);

    }



    /**
     *
     * @param file
     */
    private void loadIntervalData(File file){

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

    /**
     *
     * @param parts
     */
    protected void handleParts(String[] parts) {

       ChromosomSizes chrSizes = ChromosomSizes.getInstance();

       if(parts[0].matches("chr(\\d{1,2}|X|Y)")) { //TODO get other chromosoms
           long offset = chrSizes.offset(parts[0]); //handle null pointer exc if chromosome name is not in list

           intervalsStart.add(Long.parseLong(parts[1]) + offset);
           intervalsEnd.add(Long.parseLong(parts[2])+ offset);

           intervalName.add(parts[3]);

           if(parts.length > 4)
                intervalScore.add(Long.parseLong(parts[4]));
           else
                intervalScore.add(0L);
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

}
