package de.thm.genomeData.tracks;

import de.thm.misc.Genome;

import java.io.Serializable;

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
public interface  Track extends Serializable, Cloneable {

    long serialVersionUID = 606249588L;

    Genome.Assembly getAssembly();

    String getCellLine();

    String getDescription();

    String getName();

    long[] getStarts();

    long[] getEnds();

    int getUid();

    int getDbid();

    String getPack();

    Track clone();

    int hashCode();

    boolean equals(Object o);

    String toString();

    String getSource();

    String getSourceurl();
}
