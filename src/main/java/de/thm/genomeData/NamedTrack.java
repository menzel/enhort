package de.thm.genomeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Track object that defines interval with are identified by a name.
 *
 * Created by Michael Menzel on 26/2/16.
 */
public class NamedTrack extends Track {

    private final int uid = ++UID;
    private final String name;
    private final String description;
    private List<Long> intervalsStart;
    private List<Long> intervalsEnd;
    private List<String> intervalName;


    NamedTrack(List<Long> starts, List<Long> ends, List<String> names, String name, String description) {

        this.intervalsStart= starts;
        this.intervalsEnd= ends;
        this.intervalName= names;


        this.description = description;
        this.name = name;

        preprocess();
    }

    private void preprocess() {
        List<Long> newStart = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();
        List<String> newNames = new ArrayList<>();


        if (intervalsStart.isEmpty()) return;

        long start = intervalsStart.get(0);
        long end = intervalsEnd.get(0);
        String name = "";


        for (int i = 0; i < intervalsStart.size(); i++) {

            if (i < intervalsStart.size() - 1 && end > intervalsStart.get(i + 1)) { // overlap

                if (end < intervalsEnd.get(i + 1))
                    end = intervalsEnd.get(i + 1);
                name = intervalName.get(i) + "_" + name;

            } else {  //do not overlap
                newStart.add(start);
                newEnd.add(end);
                newNames.add(intervalName.get(i) + "_" + name);

                if (i >= intervalsStart.size() - 1) break; // do not get next points if this was the last

                start = intervalsStart.get(i + 1);
                end = intervalsEnd.get(i + 1);
            }
        }

        this.intervalsStart = newStart;
        intervalsEnd = newEnd;
        intervalName = newNames;


    }

    @Override
    public int getUid() {
        return uid;
    }


    @Override
    public Track clone() {

        return new NamedTrack(
                new ArrayList<>(intervalsStart),
                new ArrayList<>(intervalsEnd),
                new ArrayList<>(intervalName),
                name,
                description
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedTrack)) return false;

        NamedTrack interval = (NamedTrack) o;
        if (!intervalsStart.equals(interval.intervalsStart)) return false;
        if (!intervalsEnd.equals(interval.intervalsEnd)) return false;
        if (!intervalName.equals(interval.intervalName)) return false;
        return !(description != null ? !description.equals(interval.description) : interval.description != null);
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + intervalsEnd.size();
        return result;
    }

    @Override
    public List<Long> getIntervalsStart() {
        return intervalsStart;
    }

    @Override
    public List<Long> getIntervalsEnd() {
        return intervalsEnd;
    }

    public List<String> getIntervalName() {
        return intervalName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
