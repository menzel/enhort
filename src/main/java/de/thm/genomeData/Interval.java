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
public interface Interval extends Serializable, Cloneable {

   enum Type {inout, score, named}

    String getDescription();

    String getName();

    String getFilename();

    Interval clone();

    Type getType();

    List<String> getIntervalName();

    List<Long> getIntervalsStart();

    List<Long> getIntervalsEnd();

    List<Double> getIntervalScore();

    int getUid();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
