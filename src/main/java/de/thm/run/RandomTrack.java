package de.thm.run;

import de.thm.genomeData.Track;
import de.thm.misc.ChromosomSizes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by menzel on 7/14/16.
 */
public class RandomTrack extends Track {

    private final List<Long> intervalsStart = new ArrayList<>();
    private final List<Long> intervalsEnd = new ArrayList<>();

    public RandomTrack(int intervals){
        long genomeSize = ChromosomSizes.getInstance().getGenomeSize();

        int length = Math.round(genomeSize / intervals);

        for(long i = 0; i < genomeSize; i+=length){
            intervalsStart.add(i);
            intervalsEnd.add(i+length/2);
        }
    }


    @Override
    public String getDescription() {
        return "rand";
    }

    @Override
    public String getName() {
        return "Rand";
    }

    @Override
    public List<Long> getIntervalsStart() {
        return this.intervalsStart;
    }

    @Override
    public List<Long> getIntervalsEnd() {
        return this.intervalsEnd;
    }

    @Override
    public int getUid() {
        return -1;
    }

    @Override
    public Track clone() {
        return this;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
