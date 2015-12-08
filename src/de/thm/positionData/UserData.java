package de.thm.positionData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class UserData extends Sites{
        /**
     *
     * @param file
     */
    public void loadPositionsFromFile(File file){

        initMap();
        int posCount = 0;

        try(Stream<String> lines = Files.lines(file.toPath())){

            Iterator it = lines.iterator();

            while(it.hasNext()){
                String line = (String) it.next();

                positions.get(getChr(line)).add(getPosition(line));
                posCount++;

            }

            setPositionCount(posCount);
            lines.close();

        } catch (IOException e ){
            System.out.println(e);

        }
    }


    private Integer getPosition(String line) {

        return Integer.parseInt(line.split("\t")[1]);
    }

    private String getChr(String line) {
        return line.split("\t")[0];
    }
}
