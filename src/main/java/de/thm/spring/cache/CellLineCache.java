package de.thm.spring.cache;

import de.thm.genomeData.tracks.TrackPackage;
import de.thm.result.DataViewResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CellLineCache {

    private static CellLineCache instance;
    private Map<String, List<String>> newCellLinesMap;

    private CellLineCache(DataViewResult collector){

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
    }

    public static CellLineCache getInstance(DataViewResult collector){
        if(instance == null)
            instance = new CellLineCache(collector);
        //TODO check if something has changed

        return instance;
    }

    public Map<String, List<String>> getCellLines(){
        return newCellLinesMap;
    }


}
