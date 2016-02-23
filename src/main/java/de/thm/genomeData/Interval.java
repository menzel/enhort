package de.thm.genomeData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Michael Menzel on 23/2/16.
 */
public interface Interval extends Serializable, Cloneable {
    String getDescription();

    String getName();

    String getFilename();

    Interval clone();

    Intervals.Type getType();

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
