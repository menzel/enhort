package de.thm.spring.cache;

import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackPackage;
import de.thm.result.DataViewResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TrackMatrixCache {
    private static TrackMatrixCache instance;
    private static int hash;
    private List<String> trackNames;
    private List<List<Integer>> ids;
    private List<String> celllines;

    private TrackMatrixCache(DataViewResult collector) {

        // Track Names
        this.trackNames = collector.getPackages().stream()
                .flatMap(trackPackage -> trackPackage.getTrackList().stream())
                .map(Track::getName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Rows with ids for checkboxes

        this.ids = new ArrayList<>();

        List<TrackPackage> packages = collector.getPackages();
        packages.sort(Comparator.comparing(TrackPackage::getCellLine));

        for (TrackPackage pack : packages) {

            List<Integer> linkIds = new ArrayList<>();
            List<String> packNames = pack.getTrackList().stream().map(Track::getName).map(String::toLowerCase).collect(Collectors.toList());

            for (String name : trackNames) {
                name = name.toLowerCase();

                if (packNames.contains(name))
                    linkIds.add(pack.getTrackList().get(packNames.indexOf(name)).getUid());
                else
                    linkIds.add(-1);
            }

            ids.add(linkIds);
        }

        // Cell lines

        this.celllines = new ArrayList<>(collector.getCellLines().keySet()).parallelStream()
                .filter(cl -> collector.getPackages()
                        .stream()
                        .map(TrackPackage::getCellLine)
                        .anyMatch(cl::equals))
                .sorted()
                .collect(Collectors.toList());

        hash = hash();
    }

    public static TrackMatrixCache getInstance(DataViewResult collector) {
        if (instance == null || hash == instance.hash())
            instance = new TrackMatrixCache(collector);
        return instance;
    }

    private int hash() {
        return this.celllines.size() * this.trackNames.size();
    }


    public List<String> getTrackNames() {
        return trackNames;
    }

    public List<List<Integer>> getIds() {
        return ids;
    }

    public List<String> getCelllines() {
        return celllines;
    }
}
