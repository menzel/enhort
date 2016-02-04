package de.thm.positionData;

import de.thm.misc.ChromosomSizes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * @param path - file to load positions from
     */
    public UserData(Path path) {
        loadPositionsFromFile(path);
    }

    /**
     * Loads positions from a bed file
     *
     * @param path - file to load
     */
    private void loadPositionsFromFile(Path path){

        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        try(Stream<String> lines = Files.lines(path)){

            Iterator it = lines.iterator();

            while(it.hasNext()){
                String line = (String) it.next();

                positions.add(getPosition(line) + chrSizes.offset(getChr(line)));
            }

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
