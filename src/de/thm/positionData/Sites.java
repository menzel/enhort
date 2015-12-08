package de.thm.positionData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public abstract class Sites {
    /**
     *
     */

    private Map<String, ArrayList<Integer>> positions = new HashMap<>();

    /**
     *
     * @param file
     */
    public void loadPositionsFromFile(File file){

        initMap();

        try(Stream<String> lines = Files.lines(file.toPath())){

            Iterator it = lines.iterator();

            while(it.hasNext()){
                String line = (String) it.next();

                positions.get(getChr(line)).add(getPosition(line));

            }

        } catch (IOException e ){
            System.out.println(e);
        }
    }

    /**
     *
     */
    private void initMap(){

        for(int i = 1; i <= 22; i++){
            positions.put("chr"+i, new ArrayList<>());
        }

        positions.put("chrX", new ArrayList<>());
        positions.put("chrY", new ArrayList<>());

    }

    protected abstract Integer getPosition(String line);

    protected abstract String getChr(String line);



    /*
    Getter and Setter
     */

    public Map<String, ArrayList<Integer>> getPositions() {
        return positions;
    }
}
