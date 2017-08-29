package de.thm.result;

import de.thm.genomeData.tracks.TrackPackage;
import de.thm.logo.GenomeFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DataViewResult implements Serializable, Result{


    private final GenomeFactory.Assembly assembly;
    private List<TrackPackage> packages;
    private Map<String, List<String>> cellLines;

    public DataViewResult(GenomeFactory.Assembly assembly, List<TrackPackage> packages, Map<String, List<String>> cellLines) {
        this.assembly = assembly;
        this.packages = packages;
        this.cellLines = cellLines;
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }

    public List<TrackPackage> getPackages() {
        return packages;
    }

    public Map<String, List<String>> getCellLines() {
        return cellLines;
    }
}
