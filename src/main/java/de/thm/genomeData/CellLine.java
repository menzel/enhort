package de.thm.genomeData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class for cell line names.
 * The Class creates a list of cellline names together with an id.
 * The id is used through the application, however the names have to be unique, too.
 *
 * Created by menzel on 7/17/17.
 */
public class CellLine {
    private static CellLine instance;
    private Map<String, Integer> names; // all cell line names
    private Integer nextID = 0;

    private CellLine(){
        names = new HashMap<>();
        names.put("none", -1);
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

        try (Stream<String> stream = Files.lines(new File("cells").toPath())) {

            stream.forEach(line -> {
                if(line.trim().startsWith("\"name\"")){
                    String name = line.split(":")[1].split("\"")[1];
                    this.names.put(name, nextID++);
                }
            });
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
        if(this.names.containsKey(cellline)){
            return this.names.get(cellline);
        }

        throw new RuntimeException("No known Cellline with the name " + cellline);
    }

    /**
     * Returns all known cell lines
     *
     * @return list of cell lines as Strings
     */
    public List<String> getCelllines() {
        return names.keySet().stream().sorted().collect(Collectors.toList());
    }
}
