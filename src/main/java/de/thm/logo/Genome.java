package de.thm.logo;

import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        int counter = 0; //genomic position counter
        LineIterator it = null;
        String lastChr = "";
        String line = "";

        for (Long position : sublist) {
            Pair<String, Long> start = ChromosomSizes.getInstance().mapToChr(assembly, position);

            if (!lastChr.equals(start.getLeft())) {  // if new chr load the new chr file
                try {
                    Path chr = filepath.resolve(start.getLeft() + ".fa");
                    it = FileUtils.lineIterator(chr.toFile(), "UTF-8");
                    counter = 0;
                    lastChr = start.getLeft();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            long first = start.getRight() - width / 2;
            assert it != null;

            while (it.hasNext() || counter > first) { //TODO check overflow of 'it' on large numbers in GenomeFacotry

                if (counter > first) {

                    int lineStart = Math.toIntExact(first - (counter - line.length()));
                    int lineEnd = lineStart + width;

                    if (lineEnd > line.length()) { //if end is on the next line
                        int lll = line.length(); /// last line length
                        String part = line.substring(lineStart, lll);// TODO check if the next line (line) has 50 chars

                        line = it.nextLine();
                        counter += line.length();

                        try {
                            part += line.substring(0, (lineEnd - lll));
                            sequences.add(part);
                        } catch (StringIndexOutOfBoundsException e){
                            // happens when the following line has too few chars. just ignore the position then.
                        }

                    } else if (lineStart >= 0 && lineEnd > 0) {
                        sequences.add(line.substring(lineStart, lineEnd));
                    } else {
                        String s = StringUtils.repeat('N', width);
                        sequences.add(s); //TODO find out how one can get here? (negative lineStart)
                    }

                    break;
                }

                line = it.nextLine();

                if (line.startsWith(">"))
                    continue;

                counter += line.length();
            }

        }

        if(sequences.size() != sublist.size()){
            System.err.println("Genome: Not enough sequences in list (Genome.java line 130)");
        }

        if(sequences.stream().filter(s -> s.length() != width).count() > 0){
            System.err.println("Genome: Some or more sequences do not have the required width (Genome.java line 134)");
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

        //collect and remove nulls:
        List<Path> paths_list = paths.collect(Collectors.toList())
                                .stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

        for (Path path : paths_list) {

            if (!path.toFile().isFile())
                continue; //if the file is not a chr file jump to next

            System.out.println("Genome.java (getPositions) current file: " + path.toString());

            try {
                it = FileUtils.lineIterator(path.toFile(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

            String line;
            int counter = 0;

            if(it == null)
                System.err.println("it is null");
            else while (it.hasNext()) {

                line = it.nextLine();

                if (line == null) //header line in fasta files
                    continue;

                if (line.startsWith(">")) //header line in fasta files
                    continue;


                Matcher matcher = pattern.matcher(line);

                if (matcher.matches()) {
                    String chrName = path.getFileName().toString(); //get filename
                    String chr = chrName.substring(0, chrName.length() - 3); //remove .fa file ending
                    long offset;

                    try {
                        offset = chrSizes.offset(assembly, chr);

                        int logo_length = StringUtils.countMatches(logo, "["); // count [.] blocks to get logo width. TODO: replace by better calc
                        logo_length += StringUtils.countMatches(logo, "."); // count . to get logo width. TODO: replace by better calc

                        pos.add(offset + (long) (counter + matcher.group(1).length()) + logo_length / 2);
                        //String seq =  line.substring((matcher.group(1).length()), (matcher.group(1).length())  + logo_length);

                    } catch (NullPointerException e) {
                        System.err.println("unknown chr " + chr + " " + chrName);
                        break; //chr unknown, get next file
                    }
                }

                counter += line.length();

                if (pos.size() >= count)
                    break; // break from one chromosome file loop
            }

            if (pos.size() >= count)
                break; //break from loop over all files
        }

        return pos;
    }

    String getSequence(Long position, int width) {
        List<Long> list = new ArrayList<>();
        list.add(position);
        return getSequence(list, width, 2).get(0);
    }
}





