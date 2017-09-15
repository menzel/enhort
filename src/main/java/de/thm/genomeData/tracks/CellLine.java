package de.thm.genomeData.tracks;

import de.thm.genomeData.sql.DBConnector;
import de.thm.run.BackendController;

import java.util.*;
import java.util.stream.Collectors;

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


        DBConnector connector = new DBConnector();
        connector.connect();
        names = connector.getAllCellLines();

        if(BackendController.runlevel == BackendController.Runlevel.DEBUG) {
            List<String> allNames = new ArrayList<>();
            allNames.addAll(names.keySet().stream().filter(key -> names.get(key) == null).collect(Collectors.toList()));
            names.keySet().stream().filter(key -> names.get(key) != null).forEach(key -> allNames.addAll(names.get(key)));

            if (allNames.size() != allNames.stream().distinct().count()) {
                System.err.println("Cellline: There are duplicate values in the cell line names list and sublists:");

                Set<String> set = new HashSet<>();
                allNames.stream().filter(key -> !set.add(key)).forEach(System.err::println);
            }
        }
    }

    /**
     * Returns all known cell lines
     *
     * @return list of cell lines as Strings
     */
    public Map<String, List<String>> getCelllines() {
        return names;
    }


    /**
     * checks if the given cell line is known to the env. If not it throws an error,
     * if it is known, the real name is given back
     *
     * @param cellline - name of the cell line as string
     * @return real name of the cell line
     */
    public String check(String cellline) throws RuntimeException{

        //TODO impl check
        return cellline;
    }
}
