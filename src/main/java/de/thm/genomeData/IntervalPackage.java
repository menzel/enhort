package de.thm.genomeData;

import java.util.List;

/**
 * Created by Michael Menzel on 12/2/16.
 */
public class IntervalPackage {
    private final List<Track> trackList;
    private final String description;
    private final PackageName name;


    public IntervalPackage(List<Track> trackList, PackageName name, String description) {
        this.trackList = trackList;
        this.name = name;
        this.description = description;
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public String getDescription() {
        return description;
    }

    public PackageName getName() {
        return name;
    }

public enum PackageName{Basic, Expression, Histone}
}
