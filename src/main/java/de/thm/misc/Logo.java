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
        String output = "[";

        for(List<Map<String, String>> position: heights){
            output += "[";
            for(Map<String, String> base: position){
                if(Double.parseDouble(base.get("bits")) > 0.0)
                    output += "{letter:'" + base.get("letter").toUpperCase() + "', bits:" + base.get("bits") + "},";
            }

            output += "],";
        }
        return output + "];";

    }

}
