package de.thm.genomeData;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for interval data. Holds lists of start and stop positions of intervals as well as their names and scores.
 *
 * Each Interval has a uniq ID by which it is identified.
 *
 * <p>The intervals list can get very big, it is therefore neccessary to not use the list.hashCode method inside
 * this hashCode method because they take very long and slow down the algorithms. A check of the length of the list is better here</p>
 *
 * Created by Michael Menzel on 23/2/16.
 */
public abstract class Track implements Serializable, Cloneable {

    public static long serialVersionUID = 60624950L;
    public static int UID = 1;

    public abstract String getDescription();

    public abstract String getName();

    public abstract List<Long> getIntervalsStart();

    public abstract List<Long> getIntervalsEnd();

    public abstract int getUid();

    public abstract Track clone();

    public abstract int hashCode();

    public abstract boolean equals(Object o);
}