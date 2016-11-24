package de.thm.logo;

import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this.filepath = filepath;
    }


     /**
     * Return a list of sequences with a width of width that are selected by the given list of positions
     *
     * @param positions - positions to check
     * @param width - width of the sequences
     *
     *
     * @return list of sequences at sites
     */
    List<String> getSequence(List<Long> positions, int width, int count){

        List<Long> sublist = new ArrayList<>();

        if(positions.size() > count){
            //add a block from the first postions
            sublist.addAll(positions.subList(0, count/3));

            //add a block from the middle
            int start = count/3 + ((positions.size() - (count/3 + count/3) - count/3)/ 2);
            sublist.addAll(positions.subList(start, start + count/3));

            //add a block from the end
            sublist.addAll(positions.subList(positions.size()-(count/3), positions.size()));
        } else {
            sublist.addAll(positions);
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

                            } else if(lineStart >= 0 && lineEnd > 0){
                                sequences.add(line.substring(lineStart,lineEnd));
                            }

                            break;
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

    /**
     * Return a list of sequences with a width of width that are selected by the given sites object
     *
     * @param sites - positions to check
     * @param width - width of the sequences
     *
     *
     * @return list of sequences at sites
     */
    List<String> getSequence(Sites sites, int width, int count){
        return getSequence(sites.getPositions(), width,count);
    }

    public List<Long> getPositions(String logo, int count) {
        List<Long> pos = new ArrayList<>();
        Stream<Path> paths;
        LineIterator it = null;
        Pattern pattern = Pattern.compile("(\\w*)" + logo + "(\\w*)");
        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        try {
            paths = Files.walk(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Path> paths_list =  paths.collect(Collectors.toList());

        for(Path path: paths_list){

            if(!path.toFile().isFile())
                continue; //if the file is not a chr file jump to next

            try {
                it = FileUtils.lineIterator(path.toFile(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

            String line;
            int counter = 0;


            while(it.hasNext()){
                line = it.nextLine();

                if(line.startsWith(">"))
                    continue;


                Matcher matcher = pattern.matcher(line);

                if(matcher.matches()){
                    String chrName = path.getFileName().toString(); //get filename
                    String chr = chrName.substring(0, chrName.length()-3); //remove .fa file ending
                    long offset = 0;

                    try {
                        offset = chrSizes.offset(chr);
                        pos.add(offset + (long) (counter + matcher.group(1).length()) + (logo.length()/2));

                    } catch (NullPointerException e){
                        //System.err.println("unknown chr " + chr + " " + chrName);
                        break; //chr unknown, get next file
                    }

                }

                counter += line.length();

                if(pos.size() >= count)
                    break;
            }

            if(pos.size() >= count)
                break;
        }

        return pos;
    }

}
