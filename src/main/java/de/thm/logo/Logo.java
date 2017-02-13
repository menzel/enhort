package de.thm.logo;


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

    private final List<List<Map<String, String>>> heights = new ArrayList<>(); // list_(alle Pos)( list_(letters) ({letter: bits}))

    /**
     * Add the data for one position for the sequencelogo
     *
     * @param height - the heights of one position of a sequencelogo
     */
    public void add(Map<String, Double> height) {

        List<Map<String, String>> positionlist = new ArrayList<>();

        for(Map.Entry<String, Double> entry: height.entrySet()){

            Map<String, String> letter = new HashMap<>();

            letter.put("letter", entry.getKey().toLowerCase());
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

    /**
     * Returns the consensus sequence of the known sequence logo data
     *
     * @return - consensus sequence
     */
    public String getConsensus() {

        String consensus = "";

        for(List<Map<String, String>> position: heights) {
            double max = 0;
            String letter = "";

            for (Map<String, String> base : position) {
                double bits = Double.parseDouble(base.get("bits"));

                if(bits > max){
                    max = bits;
                    letter = base.get("letter").toUpperCase();
                }
            }
            if(max > 0.01)
                consensus += letter;
            else consensus += ".";
        }

        return consensus;
    }

    public String getRegex() {
        String regex = "[";

        for(List<Map<String, String>> position: heights) {
            String letter;

            for (Map<String, String> base : position) {
                double bits = Double.parseDouble(base.get("bits"));

                if(bits > 0.01){
                    letter = base.get("letter").toUpperCase();
                    regex += letter;
                }
            }

            if(regex.substring(regex.length()-1).equals("[")) {
                regex = regex.substring(0, regex.length() - 1);
                regex += ".[";

            } else {
                regex += "][";
            }
        }

        System.out.println("In Logo.java: " + regex);
        return regex.substring(0, regex.length()-1);


    }
}
