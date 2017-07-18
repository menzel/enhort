package de.thm.genomeData;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Class for cell line names.
 * The Class creates a list of cellline names together with an id.
 * The id is used through the application, however the names have to be unique, too.
 *
 * Created by menzel on 7/17/17.
 */
public class CellLine {
    private static CellLine instance;
    private SortedMap<String, List<String>> names; // all cell line names

    private CellLine(){
        names = new TreeMap<>();
        names.put("none", null);
        load();
    }

    public static CellLine getInstance(){
        if(instance  == null)
            instance = new CellLine();
        return instance;
    }

    /**
     * loads the cells file
     */
    private void load() {

        try {
            List<String> lines = Files.readAllLines(new File("cells").toPath());

            JSONObject all = new JSONObject(StringUtils.join(lines, ""));
            JSONArray cellines = all.getJSONArray("celllines");

            for(int i = 0; i < cellines.length(); i++){

                if(cellines.getJSONObject(i).has("sub")){
                    JSONArray sublines = cellines.getJSONObject(i).getJSONArray("sub");
                    List<String> subnames = new ArrayList<>();

                    for(int j = 0; j < sublines.length(); j++)  //add subnames
                        subnames.add(sublines.getJSONObject(j).getString("name"));

                    names.put(cellines.getJSONObject(i).getString("name"), subnames);


                } else {
                    names.put(cellines.getJSONObject(i).getString("name"), null);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns the cell line id of a given cellline name
     *
     * @param cellline - name of the cell line
     * @return id of the given cell line
     */
    public int valueOf(String cellline) {
        for(String key: names.keySet()){

            if(key.equals(cellline))
                return key.hashCode();

            if(names.get(key) != null) //if there is a sublist
                for(String inner: names.get(key))
                    if (inner.equals(cellline))
                        return inner.hashCode();
        }

        throw new RuntimeException("No known Cellline with the name " + cellline);
    }

    /**
     * Returns all known cell lines
     *
     * @return list of cell lines as Strings
     */
    public Map<String, List<String>> getCelllines() {
        return names;
    }
}
