package de.thm.misc;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for a sequence logo
 *
 * Created by menzel on 11/9/16.
 */
public final class Logo implements Serializable{

    private final List<List<Map<String, String>>> heights = new ArrayList<>();

    /**
     * Add the data for one position for the sequencelogo
     *
     * @param height - the heights of one position of a sequencelogo
     */
    public void add(Map<String, Double> height) {

        List<Map<String, String>> positionlist = new ArrayList<>();

        for(Map.Entry<String, Double> entry: height.entrySet()){

            Map<String, String> letter = new HashMap<>();

            letter.put("letter", entry.getKey());
            letter.put("bits", String.valueOf(entry.getValue()));


            positionlist.add(letter);

        }

        heights.add(positionlist);
    }


    /**
     * Returns the heights for letters of the sequencelogo as JSONArray
     *
     * @return - heights of letters for sequencelogo
     */
    public JSONArray getHeights() {
        JSONArray hg = new JSONArray();

        for(List<Map<String, String>> position: heights) {
            JSONArray pos = new JSONArray();

            for (Map<String, String> base : position) {

                if (Double.parseDouble(base.get("bits")) > 0.0) {
                    JSONObject bs = new JSONObject();

                    bs.put("letter", base.get("letter").toUpperCase());
                    bs.put("bits", Double.parseDouble(base.get("bits")));

                    pos.put(bs);
                }
            }

            hg.put(pos);
        }

        return hg;

    }

    @Override
    public String toString() {
        return heights.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Logo logo = (Logo) o;

        return heights.equals(logo.heights);

    }

    @Override
    public int hashCode() {
        return heights.hashCode();
    }

    public List<List<Map<String, String>>> getValues() {
        return this.heights;
    }
}
