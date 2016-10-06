package de.thm.genomeData;

import java.io.Serializable;
import java.util.List;
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

    public static long serialVersionUID = 606249588L;
    static AtomicInteger UID = new AtomicInteger(1);

    public abstract Assembly getAssembly();

    public abstract  CellLine getCellLine();

    public abstract String getDescription();

    public abstract String getName();

    public abstract List<Long> getIntervalsStart();

    public abstract List<Long> getIntervalsEnd();

    public abstract int getUid();

    @Override
    public abstract Track clone();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object o);

    public enum Assembly {hg19, hg38}

    public enum CellLine {HeLa, hESC, none}
}
