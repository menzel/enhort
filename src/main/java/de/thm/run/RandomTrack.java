package de.thm.run;

import de.thm.calc.GenomeFactory;
import de.thm.genomeData.Track;
import de.thm.misc.ChromosomSizes;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to create random tracks for testing run times
 *
 * Created by menzel on 7/14/16.
 */
class RandomTrack extends Track {

    private final List<Long> intervalsStart = new ArrayList<>();
    private final List<Long> intervalsEnd = new ArrayList<>();

    RandomTrack(int intervals){
        long genomeSize = ChromosomSizes.getInstance().getGenomeSize();

        int length = Math.round(genomeSize / intervals);

        for(long i = 0; i < genomeSize; i+=length){
            intervalsStart.add(i);
            intervalsEnd.add(i+length/2);
        }
    }


    @Override
    public GenomeFactory.Assembly getAssembly() {
        return null;
    }

    @Override
    public Track.CellLine getCellLine() {
        return null;
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
    public List<Long> getStarts() {
        return this.intervalsStart;
    }

    @Override
    public List<Long> getEnds() {
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
