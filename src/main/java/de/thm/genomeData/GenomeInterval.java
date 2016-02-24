package de.thm.genomeData;

import de.thm.genomeData.Intervals.Type;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.PositionPreprocessor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
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
public final class GenomeInterval implements Interval {

    private static final long serialVersionUID = 1690225L;
    private static int UID = 1;
    private final int uid = ++UID;
    private final String filename;
    private List<Long> intervalsStart;
    private List<Long> intervalsEnd;
    private List<String> intervalName;
    private List<Double> intervalScore;
    private Intervals.Type type;
    private String name;
    private String description;

     /**
     * Constructor for Test Intervals
     */
    public GenomeInterval(){
        filename = "testfilename";
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
    GenomeInterval(File file, Type type, String filename) {

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

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name){this.name = name;}

    @Override
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


    @Override
    public Interval clone() {
        GenomeInterval copy = new GenomeInterval();

        copy.setIntervalsStart(new ArrayList<>(intervalsStart));
        copy.setIntervalsEnd(new ArrayList<>(intervalsEnd));
        copy.setType(this.type);

        return copy;
    }

    @Override
    public Type getType() { return type; }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public List<String> getIntervalName() {
        return intervalName;
    }

    public void setIntervalName(List<String> intervalName) {
        this.intervalName = intervalName;
    }

    @Override
    public List<Long> getIntervalsStart() {
        return intervalsStart;
    }

    public void setIntervalsStart(List<Long> intervalsStart) {
        this.intervalsStart = intervalsStart;
    }

    @Override
    public List<Long> getIntervalsEnd() {
        return intervalsEnd;
    }

    public void setIntervalsEnd(List<Long> intervalsEnd) {
        this.intervalsEnd = intervalsEnd;
    }

    @Override
    public List<Double> getIntervalScore() {
        return intervalScore;
    }

    public void setIntervalScore(double prob) {
        intervalScore = new ArrayList<>();

        for(int i = 0; i < intervalsStart.size(); i++){
            intervalScore.add(prob);
        }

    }

    public void setIntervalScore(List<Double> intervalScore) {
        this.intervalScore = intervalScore;
    }

    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenomeInterval)) return false;

        GenomeInterval interval = (GenomeInterval) o;

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
        int result = uid;
        result = 31 * result + type.hashCode();
        result = 31 * result + intervalsEnd.size();
        return result;
    }
}