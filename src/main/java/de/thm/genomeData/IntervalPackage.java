package de.thm.genomeData;

import java.util.List;

/**
 * Created by Michael Menzel on 12/2/16.
 */
public class IntervalPackage {
    private List<Interval> intervalList;
    private String description;
    private PackageName name;


    public IntervalPackage(List<Interval> intervalList, PackageName name, String description) {
        this.intervalList = intervalList;
        this.name = name;
        this.description = description;
    }

    public List<Interval> getIntervalList() {
        return intervalList;
    }

    public String getDescription() {
        return description;
    }

    public PackageName getName() {
        return name;
    }

public enum PackageName{Basic, Expression, Histone}
}
