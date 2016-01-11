package de.thm.positionData;

import de.thm.misc.ChromosomSizes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Handles position data supplied in a file by a user
 *
 * Created by Michael Menzel on 8/12/15.
 */
public class UserData extends Sites{

    /**
     * Constructor
     * @param file - file to load positions from
     */
    public UserData(File file) {
        loadPositionsFromFile(file);
    }

    /**
     * Loads positions from a bed file
     *
     * @param file - file to load
     */
    private void loadPositionsFromFile(File file){

        int posCount = 0;
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        try(Stream<String> lines = Files.lines(file.toPath())){

            Iterator it = lines.iterator();

            while(it.hasNext()){
                String line = (String) it.next();

                positions.add(getPosition(line) + chrSizes.offset(getChr(line)));
                posCount++;

            }

            setPositionCount(posCount);
            lines.close();

        } catch (IOException e ){
            System.out.println(e);

        }

        Collections.sort(positions);
    }

    private Long getPosition(String line) {
        return Long.parseLong(line.split("\t")[1]);
    }

    private String getChr(String line) {
        return line.split("\t")[0];
    }
}
