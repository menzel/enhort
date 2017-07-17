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
 * Created by menzel on 7/17/17.
 */
public class CellLine {
    private static CellLine instance;
    private Map<String, Integer> names;
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
     *
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
     *
     * @param cellline
     * @return
     */
    public int valueOf(String cellline) {
        if(this.names.containsKey(cellline)){
            return this.names.get(cellline);
        }

        throw new RuntimeException("No known Cellline with the name " + cellline);
    }

    public List<String> getCelllines() {
        return names.keySet().stream().sorted().collect(Collectors.toList());
    }
}
