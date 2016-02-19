package de.thm.genomeData;

import de.thm.calc.PositionPreprocessor;
import de.thm.misc.ChromosomSizes;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class Interval implements Serializable, Cloneable{

    private static final long serialVersionUID = 1690225L;
    private static int UID = 1;
    protected List<Long> intervalsStart;
    protected List<Long> intervalsEnd;
    protected List<String> intervalName;
    protected List<Double> intervalScore;
    protected Type type;
    private int uid = ++UID;
    private String name;
    private String filename;

    private String description;

     /**
     * Constructor for Test Intervals
     */
    public Interval(){
        intervalsStart = new ArrayList<>();
        intervalsEnd = new ArrayList<>();
        intervalName = new ArrayList<>();
        intervalScore = new ArrayList<>();
    }

     /**
     * Constructor for Intervals
     * @param file - file to parse for data
     * @param type - Type of data (inout, score, named)
     */
    public Interval(File file, Type type, String filename) {

        this.type = type;
        this.filename = filename;

        try {
            int length = new Long(Files.lines(file.toPath()).count()).intValue();

            intervalsStart = new ArrayList<>(length);
            intervalsEnd = new ArrayList<>(length);
            intervalName = new ArrayList<>(length);
            intervalScore = new ArrayList<>(length);

        } catch (IOException e) {
            e.printStackTrace();
        }


        loadIntervalData(file);

        if(type == Type.inout)
            PositionPreprocessor.preprocessData(this);

    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    /**
     * Loads interval data from a bed file. Calls handleParts to handle each line
     *
     * @param file - file to parse
     */
    private void loadIntervalData(File file){

        try(Stream<String> lines = Files.lines(file.toPath(), StandardCharsets.UTF_8)){
            Iterator<String> it = lines.iterator();

            Pattern header = Pattern.compile("track fullname=.(.*). description=.(.*).."); //TODO . are "

            while(it.hasNext()){
                String line = it.next();
                Matcher matcher = header.matcher(line);

                if(matcher.matches()){
                    name = matcher.group(1);
                    description = matcher.group(2);

                } else if (true){ //TODO build another matcher for normal lines
                    String[] parts = line.split("\t");
                    handleParts(parts);
                }
            }

            lines.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Handling for each line in the bed file
     *
     * @param parts - parts of the line splitted at seperator
     */
    protected void handleParts(String[] parts) {

       ChromosomSizes chrSizes = ChromosomSizes.getInstance();

       if(parts[0].matches("chr(\\d{1,2}|X|Y)")) { //TODO get other chromosoms
           long offset = chrSizes.offset(parts[0]); //handle null pointer exc if chromosome name is not in list

           intervalsStart.add(Long.parseLong(parts[1]) + offset);
           intervalsEnd.add(Long.parseLong(parts[2])+ offset);

           intervalName.add(parts[3].intern());


           if(parts.length > 4 && StringUtils.isNumeric(parts[4]))
                intervalScore.add(Double.parseDouble(parts[4]));
           else
                intervalScore.add(.0);
       }
    }

    public Interval invert() {
        if(intervalsStart.size() == 0)
            return this.copy();

        Interval tmp = new Interval();

        tmp.setIntervalsStart(new ArrayList<>(intervalsEnd));
        tmp.setIntervalsEnd(new ArrayList<>(intervalsStart));
        tmp.setType(this.type);

        if(intervalsStart.get(0) != 0L) {
            tmp.getIntervalsStart().add(0, 0L);
        } else {
            tmp.getIntervalsEnd().remove(0);
        }

        if(intervalsEnd.get(intervalsEnd.size()-1) == ChromosomSizes.getInstance().getGenomeSize()) {
            tmp.getIntervalsStart().remove(tmp.getIntervalsStart().size()-1);

        } else {
            tmp.getIntervalsEnd().add(ChromosomSizes.getInstance().getGenomeSize());
        }


        return tmp;
    }

    public Interval copy() {
        Interval copy = new Interval();

        copy.setIntervalsStart(new ArrayList<>(intervalsStart));
        copy.setIntervalsEnd(new ArrayList<>(intervalsEnd));
        copy.setType(this.type);

        return copy;
    }

    public Type getType() { return type; }

    public void setType(Type type) {
        this.type = type;
    }

    public List<String> getIntervalName() {
        return intervalName;
    }

    public void setIntervalName(List<String> intervalName) {
        this.intervalName = intervalName;
    }

    public List<Long> getIntervalsStart() {
        return intervalsStart;
    }

    public void setIntervalsStart(List<Long> intervalsStart) {
        this.intervalsStart = intervalsStart;
    }

    public List<Long> getIntervalsEnd() {
        return intervalsEnd;
    }

    public void setIntervalsEnd(List<Long> intervalsEnd) {
        this.intervalsEnd = intervalsEnd;
    }

    public List<Double> getIntervalScore() {
        return intervalScore;
    }

    public void setIntervalScore(List<Double> intervalScore) {
        this.intervalScore = intervalScore;
    }

    public void setIntervalScore(double prob) {
        intervalScore = new ArrayList<>();

        for(int i = 0; i < intervalsStart.size(); i++){
            intervalScore.add(prob);
        }

    }

    public int getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interval)) return false;

        Interval interval = (Interval) o;

        if (!intervalsStart.equals(interval.intervalsStart)) return false;
        if (!intervalsEnd.equals(interval.intervalsEnd)) return false;
        if (intervalName != null ? !intervalName.equals(interval.intervalName) : interval.intervalName != null)
            return false;
        if (intervalScore != null ? !intervalScore.equals(interval.intervalScore) : interval.intervalScore != null)
            return false;
        if (type != interval.type) return false;
        return !(description != null ? !description.equals(interval.description) : interval.description != null);

    }

    @Override
    public int hashCode() {
        int result = intervalsStart.hashCode();
        result = 31 * result + intervalsEnd.hashCode();
        result = 31 * result + (intervalName != null ? intervalName.hashCode() : 0);
        result = 31 * result + (intervalScore != null ? intervalScore.hashCode() : 0);
        result = 31 * result + type.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + filename.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public enum Type {inout, score, named}
}
