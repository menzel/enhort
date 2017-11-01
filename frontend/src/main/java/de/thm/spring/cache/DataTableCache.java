package de.thm.spring.cache;

import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackPackage;
import de.thm.result.DataViewResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataTableCache {

    private static DataTableCache instance;
    private Map<String, List<String>> newCellLinesMap; //contains a list of celllines, with either null or another list as child. The containing cell lines are the intersect between the known cellines from the celllines-file and the celllines given by loaded tracks
    private static Integer lastCollectorHash;
    private List<String> trackNames;

    private DataTableCache(DataViewResult collector){

        // Cell lines list

        this.newCellLinesMap = new HashMap<>();
        List<String> knownCelllines = collector.getPackages().stream().map(TrackPackage::getCellLine).collect(Collectors.toList());
        Map<String, List<String>> cellLines = collector.getCellLines();

        List<String> flatCellLines = new ArrayList<>();
        cellLines.keySet().stream().filter(k -> cellLines.get(k) != null).forEach(k -> flatCellLines.addAll(cellLines.get(k)));

        for(String cl: knownCelllines) { //iterate over all cell lines given by tracks
            if (cellLines.keySet().contains(cl)) { // if the cell line is in the upper list
                if (cellLines.get(cl) == null) // if there is no sub-list
                    newCellLinesMap.put(cl, null);
                else // if there is a sub list (should not happen in most cases):
                    newCellLinesMap.put(cl, cellLines.get(cl).stream()
                                    .filter(knownCelllines::contains)
                                    .collect(Collectors.toList()));
            } else if (flatCellLines.contains(cl)) { // if the cell line is in any sub-list

                for (String key : cellLines.keySet())// iterate over all celllines (each time)
                    if (cellLines.get(key) != null)
                        if (cellLines.get(key).contains(cl))
                            newCellLinesMap.put(key, cellLines.get(key).stream() // add all sub cell lines together with upper name
                                    .filter(knownCelllines::contains)
                                    .collect(Collectors.toList()));
            }
        }

        // track names

        trackNames = collector.getPackages().stream()
                .flatMap(trackPackage -> trackPackage.getTrackList().stream())
                .map(Track::getName)
                .distinct() //TODO filter case insensitiv
                .sorted()
                .collect(Collectors.toList());
    }

    public static DataTableCache getInstance(DataViewResult collector){
        if(instance == null || ! hashCollector(collector).equals(lastCollectorHash)) {

            instance = new DataTableCache(collector);
            lastCollectorHash = hashCollector(collector);
        }

        return instance;
    }

    private static Integer hashCollector(DataViewResult collector) {
        return collector.getCellLines().size() + collector.getPackages().size() + collector.getAssembly().hashCode();
    }

    public Map<String, List<String>> getCellLines(){
        return newCellLinesMap;
    }


    public List<String> getTrackNames() {
        return this.trackNames;
    }
}
