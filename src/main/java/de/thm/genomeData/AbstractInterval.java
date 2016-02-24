package de.thm.genomeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract skeletal class.
 *
 * Created by Michael Menzel on 24/2/16.
 */
public abstract class AbstractInterval implements Interval{

    private static final long serialVersionUID = 30624951L;
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

    AbstractInterval(List<Long> starts, List<Long> ends, List<String> names, List<Double> scores, String filename) {

        this.filename = filename;
        intervalsStart = starts;
        intervalsEnd = ends;
        intervalName = names;
        intervalScore = scores;

    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }


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


    @Override
    public List<String> getIntervalName() { return intervalName; }


    @Override
    public List<Long> getIntervalsStart() { return intervalsStart; }


    @Override
    public List<Long> getIntervalsEnd() { return intervalsEnd; }


    @Override
    public List<Double> getIntervalScore() { return intervalScore; }


    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interval)) return false;

        Interval interval = (Interval) o;

        if (!intervalsStart.equals(interval.getIntervalsStart())) return false;
        if (!intervalsEnd.equals(interval.getIntervalsEnd())) return false;
        if (intervalName != null ? !intervalName.equals(interval.getIntervalName()) : interval.getIntervalName()!= null)
            return false;
        if (intervalScore != null ? !intervalScore.equals(interval.getIntervalScore()) : interval.getIntervalScore()!= null)
            return false;
        if (type != interval.getType()) return false;
        return !(description != null ? !description.equals(interval.getDescription()) : interval.getDescription()!= null);

    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + type.hashCode();
        result = 31 * result + intervalsEnd.size();
        return result;
    }
}
