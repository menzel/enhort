package de.thm.genomeData.tracks;

import de.thm.misc.Genome;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A track package has a list of tracks.
 *
 * Created by Michael Menzel on 12/2/16.
 */
public final class TrackPackage implements Serializable{
    private final List<Track> trackList;
    private final String name;
    private final Genome.Assembly assembly;
    private final String cellLine;


    TrackPackage(String name, Genome.Assembly assembly, String cellLine) {
        this.trackList = new ArrayList<>();
        this.name = name;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }


    public void add(Track track){
        this.trackList.add(track);
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public String getName() {
        return name;
    }

    public Genome.Assembly getAssembly() {
        return assembly;
    }

    public String getCellLine() {
        return cellLine;
    }
}
