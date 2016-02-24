package de.thm.genomeData;

import de.thm.misc.PositionPreprocessor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * Created by Michael Menzel on 8/12/15.
 */
public final class GenomeInterval implements Interval  {

    private static final long serialVersionUID = 60624950L;
    private static int UID = 1;
    private final int uid = ++UID;

    private final String filename;
    private List<Long> intervalsStart;
    private List<Long> intervalsEnd;
    private List<String> intervalName;
    private List<Double> intervalScore;
    private Type type;
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


    GenomeInterval(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String name, String filename, String description, Type type) {

        this.filename = filename;
        intervalsStart = starts;
        intervalsEnd = ends;
        intervalName = names;
        intervalScore = scores;
        this.description = description;
        this.name = name;
        this.type = type;

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

    @Override
    public Interval clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        GenomeInterval copy = new GenomeInterval();

        copy.setIntervalsStart(new ArrayList<>(intervalsStart));
        copy.setIntervalsEnd(new ArrayList<>(intervalsEnd));
        copy.setType(this.type);

        return copy;
    }

    @Override
    public Type getType() { return type; }

    public void setType(Type type) { this.type = type; }

    @Override
    public List<String> getIntervalName() { return intervalName; }

    public void setIntervalName(List<String> intervalName) { this.intervalName = intervalName; }

    @Override
    public List<Long> getIntervalsStart() { return intervalsStart; }

    public void setIntervalsStart(List<Long> intervalsStart) { this.intervalsStart = intervalsStart; }

    @Override
    public List<Long> getIntervalsEnd() { return intervalsEnd; }

    public void setIntervalsEnd(List<Long> intervalsEnd) { this.intervalsEnd = intervalsEnd; }

    @Override
    public List<Double> getIntervalScore() { return intervalScore; }

    public void setIntervalScore(List<Double> intervalScore) { this.intervalScore = intervalScore; }

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
