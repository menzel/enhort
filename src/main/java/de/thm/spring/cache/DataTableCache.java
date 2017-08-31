package de.thm.spring.cache;

import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackPackage;
import de.thm.result.DataViewResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataTableCache {

    private static DataTableCache instance;
    private Map<String, List<String>> newCellLinesMap;
    private static Integer lastCollectorHash;
    private List<String> trackNames;

    private DataTableCache(DataViewResult collector){


        // Cell lines

        this.newCellLinesMap = new HashMap<>();
        List<String> knownCelllines = collector.getPackages().stream().map(TrackPackage::getCellLine).collect(Collectors.toList());
        Map<String, List<String>> cellLines = collector.getCellLines();

        for (String cellline : cellLines.keySet()) {

            if (cellLines.get(cellline) != null) {
                List<String> subs = cellLines.get(cellline);
                subs = subs.stream().filter(knownCelllines::contains).collect(Collectors.toList());

                if(!subs.isEmpty())
                    newCellLinesMap.put(cellline, subs);

            } else {
                if(knownCelllines.contains(cellline))
                    newCellLinesMap.put(cellline, null);
            }
        }

        // track names

        trackNames = collector.getPackages().stream()
                .flatMap(trackPackage -> trackPackage.getTrackList().stream())
                .map(Track::getName)
                .distinct()
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
        return collector.getCellLines().size() + collector.getCellLines().size() + collector.getAssembly().hashCode();
    }

    public Map<String, List<String>> getCellLines(){
        return newCellLinesMap;
    }


    public List<String> getTrackNames() {
        return this.trackNames;
    }
}
