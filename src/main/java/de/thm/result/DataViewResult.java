package de.thm.result;

import de.thm.genomeData.CellLine;
import de.thm.genomeData.Track;
import de.thm.logo.GenomeFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DataViewResult implements Serializable, Result{


    private final GenomeFactory.Assembly assembly;
    private final List<Track> tracks;
    private final Map<String, List<String>> knownCelllines;

    public DataViewResult(GenomeFactory.Assembly assembly, List<Track> tracks) {
        this.assembly = assembly;
        this.tracks = tracks;
        knownCelllines = CellLine.getInstance().getCelllines();
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public Map<String, List<String>> getCellLines() {

        return knownCelllines;
    }
}
