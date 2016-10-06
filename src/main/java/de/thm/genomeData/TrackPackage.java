package de.thm.genomeData;

import java.util.List;

/**
 * A track package has a list of tracks. The packages are identified by the enum PackageName
 *
 * Created by Michael Menzel on 12/2/16.
 */
public final class TrackPackage {
    private final List<Track> trackList;
    private final String description;
    private final PackageName name;
    private final Track.Assembly assembly;

    TrackPackage(List<Track> trackList, PackageName name, String description, Track.Assembly assembly) {
        this.trackList = trackList;
        this.name = name;
        this.description = description;
        this.assembly = assembly;
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

    public Track.Assembly getAssembly() {
        return assembly;
    }

    public enum PackageName {Basic, Expression, Histone, Repeats_by_name, TFBS, OpenChrom, Distance, Restriction_sites}
}
