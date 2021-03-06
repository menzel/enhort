// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.genomeData.tracks;

import de.thm.genomeData.sql.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private volatile static CellLine instance;
    private SortedMap<String, List<String>> names; // all cell line names
    private final Logger logger = LoggerFactory.getLogger(CellLine.class);

    private CellLine(){
        load();
    }

    public synchronized static CellLine getInstance() {
        if(instance  == null)
            instance = new CellLine();
        return instance;
    }

    /**
     * loads the cells file
     */
    private synchronized void load() {

        DBConnector connector = new DBConnector();
        connector.connect();
        names = connector.getAllCellLines();

        if(logger.isDebugEnabled()) {
            List<String> allNames = new ArrayList<>();
            allNames.addAll(names.keySet().stream().filter(key -> names.get(key) == null).collect(Collectors.toList()));
            names.keySet().stream().filter(key -> names.get(key) != null).forEach(key -> allNames.addAll(names.get(key)));

            if (allNames.size() != allNames.stream().distinct().count()) {
                logger.warn("Cellline: There are duplicate values in the cell line names list and sublists:");

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
        return new TreeMap<>(names);
    }


    /**
     * checks if the given cell line is known to the env. If not it throws an error,
     * if it is known, the real name is given back
     *
     * @param cellline - name of the cell line as string
     * @return real name of the cell line
     */
    public String check(String cellline) throws IllegalArgumentException {

        //TODO impl check
        return cellline;
    }
}
