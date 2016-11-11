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
 * Stores the information for a genome.
 *
 * Gets sequences by positons
 *
 * Created by menzel on 11/4/16.
 */
final class Genome {

    private Path filepath;

    Genome(Path filepath){
        // write external tool which is called from java to get sequence logo
        this.filepath = filepath;
    }


    /**
     * Return a list of sequences with a width of width that are selected by the given list of positions
     *
     * @param sites - positions to check
     * @param width - width of the sequences
     *
     * @return list of sequences at sites
     */
    List<String> getSequence(Sites sites, int width){

        List<Long> sublist = new ArrayList<>();

        int count = 3000;

        if(sites.getPositionCount() > count){
            //add a block from the first postions
            sublist.addAll(sites.getPositions().subList(0, count/3));

            //add a block from the middle
            int start = count/3 + ((sites.getPositionCount() - (count/3 + count/3) - count/3)/ 2);
            sublist.addAll(sites.getPositions().subList(start, start + count/3));

            //add a block from the end
            sublist.addAll(sites.getPositions().subList(sites.getPositionCount()-(count/3), sites.getPositionCount()));
        }


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

                    assert it != null;
                    while (it.hasNext() || counter > first) {

                        if (counter > first) {
                            int lineStart = first - (counter - line.length());
                            int lineEnd = lineStart + width;

                            if(lineEnd > 50){ //if end is on the next line
                                String part = line.substring(lineStart, 50);

                                line = it.nextLine();
                                part += line.substring(0, lineEnd-50);

                                counter += line.length();

                                sequences.add(part);

                                break;
                            } else {
                                if(lineStart >= 0 && lineEnd > 0)
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
