package de.thm.genomeData;

import java.util.List;

/**
 * Created by Michael Menzel on 12/2/16.
 */
public class IntervalPackage {
    private List<Interval> intervalList;
    private String name;
    private String description;


    public IntervalPackage(List<Interval> intervalList, String name, String description) {
        this.intervalList = intervalList;
        this.name = name;
        this.description = description;
    }

    public List<Interval> getIntervalList() {
        return intervalList;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
