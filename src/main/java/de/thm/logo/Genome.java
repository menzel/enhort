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

    private final Path filepath;
    private GenomeFactory.Assembly assembly;

    Genome(GenomeFactory.Assembly assembly, Path filepath){
        this.filepath = filepath;
        this.assembly = assembly;
    }


     /**
     * Return a list of sequences with a width of width that are selected by the given list of positions
     *
     * @param positions - positions to check
     * @param width - width of the sequences
     * @param count -  count of positions to look up. One third of count is taken from each the beginning, the middle and the end.
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


            Pair<String, Long> start = ChromosomSizes.getInstance().mapToChr(assembly, position);
            Pair<String, Long> end = ChromosomSizes.getInstance().mapToChr(assembly, position);

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
     * @param count -  count of positions to look up. One third of count is taken from each the beginning, the middle and the end.
     *
     * @return list of sequences at sites
     */
    List<String> getSequence(Sites sites, int width, int count){
        return getSequence(sites.getPositions(), width,count);
    }


    /**
     * Get positions by given Logo @see logo.Logo
     *
     * @param logo - given Logo
     * @param count - count of positions to find
     *
     * @return positions found
     */
    public List<Long> getPositions(Logo logo, int count) {
        //TODO
        return null;
    }


    /**
     * Returns a list of 'count' sequence positions that show the exact representation of the given logo.
     *
     * @param logo - logo to search
     * @param count - count of positions to get
     *
     * @return list of positions
     */
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

                if(line.startsWith(">")) //header line in fasta files
                    continue;


                Matcher matcher = pattern.matcher(line);

                if(matcher.matches()){
                    String chrName = path.getFileName().toString(); //get filename
                    String chr = chrName.substring(0, chrName.length()-3); //remove .fa file ending
                    long offset;

                    try {
                        offset = chrSizes.offset(assembly, chr);
                        pos.add(offset + (long) (counter + matcher.group(1).length()) + (logo.length()/2));

                    } catch (NullPointerException e){
                        //System.err.println("unknown chr " + chr + " " + chrName);
                        break; //chr unknown, get next file
                    }

                }

                counter += line.length();

                if(pos.size() >= count)
                    break; // break from one chromosome file loop
            }

            if(pos.size() >= count)
                break; //break from loop over all files
        }

        return pos;
    }

}