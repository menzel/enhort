package de.thm.genomeData.tracks;
import de.thm.misc.Genome;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Interface for interval data. Holds lists of start and stop positions of intervals as well as their names and scores.
 * <p>
 * Each Interval has a uniq ID by which it is identified.
 * <p>
 * <p>The intervals list can get very big, it is therefore neccessary to not use the list.hashCode method inside
 * this hashCode method because they take very long and slow down the algorithms. A check of the length of the list is better here</p>
 * <p>
 * Created by Michael Menzel on 23/2/16.
 */
public abstract class Track implements Serializable, Cloneable {

    static final AtomicInteger UID = new AtomicInteger(1);
    public static long serialVersionUID = 606249588L;

    public abstract Genome.Assembly getAssembly();

    public abstract String getCellLine();

    public abstract String getDescription();

    public abstract String getName();

    public abstract long[] getStarts();

    public abstract long[] getEnds();

    public abstract int getUid();

    @Override
    public abstract Track clone();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public String toString() {
        return getName();
    }
}
