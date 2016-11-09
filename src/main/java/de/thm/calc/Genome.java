package de.thm.calc;

import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by menzel on 11/4/16.
 */
public class Genome {

    private Path filepath;


    public Genome(Path filepath){
        // write external tool which is called from java to get sequence logo
        this.filepath = filepath;
    }



    public List<String> getSequence(Sites sites, int width){

        List<Long> sublist = sites.getPositions();

        if(sites.getPositionCount() > 5000)
            sublist = sites.getPositions().subList(0, 5000);

        List<String> sequences = new ArrayList<>();
        int counter = 0;
        LineIterator it = null;
        String lastChr = "";
        String line = "";

        for(Long position: sublist) {


            Pair<String, Long> start = ChromosomSizes.getInstance().mapToChr(position);
            Pair<String, Long> end = ChromosomSizes.getInstance().mapToChr(position);

            if (start.getLeft().equals(end.getLeft())) { //if start and end are on the same chr


                if(!lastChr.equals(start.getLeft())) {

                    try {
                        Path chr = filepath.resolve(start.getLeft() + ".fa");
                        it = FileUtils.lineIterator(chr.toFile(), "UTF-8");
                        counter = 0;
                        lastChr = start.getLeft();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                int first = Math.toIntExact(start.getRight()) - width/2;

                if (first < Math.toIntExact(end.getRight())) {

                    while (it.hasNext() || counter > first) {

                        if (counter > first) {
                            int lineStart = first - (counter-50);
                            int lineEnd = lineStart + width;

                            if(lineEnd > 50){ //if end is on the next line
                                String part = line.substring(lineStart, 50);

                                line = it.nextLine();
                                part += line.substring(0, lineEnd-50);

                                counter += line.length();

                                sequences.add(part);

                                break;
                            } else {
                                sequences.add(line.substring(lineStart,lineEnd));
                                break;
                            }
                        }


                        line = it.nextLine();

                        if(line.startsWith(">"))
                            continue;

                        counter += line.length();
                    }
                }
            }
        }

        return sequences;
    }
}
