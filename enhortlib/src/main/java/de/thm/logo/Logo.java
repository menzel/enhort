package de.thm.logo;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Logo extends Serializable {
    /**
     * Add the data for one position for the sequencelogo
     *
     * @param height - the heights of one position of a sequencelogo
     */
    void add(Map<String, Double> height);

    /**
     * Returns the heights for letters of the sequencelogo as JSONArray
     *
     * @return - heights of letters for sequencelogo
     */
    JSONArray getHeights();

    List<List<Map<String, String>>> getValues();

    /**
     * Returns the consensus sequence of the known sequence logo data
     *
     * @return - consensus sequence
     */
    String getConsensus();

    String getRegex();

    String getName();

    void setName(String name);
}
