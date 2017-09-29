package de.thm.positionData;

import de.thm.logo.GenomeFactory;
import de.thm.misc.ChromosomSizes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Handles position data supplied in a file by a user
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
public final class UserData implements Sites {

    private final GenomeFactory.Assembly assembly;
    private List<Long> positions = new ArrayList<>();
    private List<Character> strand = new ArrayList<>();
    private String filename;


    /**
     * Constructor
     *
     * @param path - file to load positions from
     */
    public UserData(GenomeFactory.Assembly assembly, Path path) {
        this.assembly = assembly;
        loadPositionsFromFile(path);

        if (path.toFile().getName().length() > 37)
            filename = path.toFile().getName().substring(0, path.toFile().getName().length() - 37);// remove uuid appendix
        else
            filename = path.toFile().getName();
    }

    /**
     * Loads positions from a bed file
     *
     * @param path - file to load
     */
    private void loadPositionsFromFile(Path path) {

        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        Pattern entry = Pattern.compile("(chr(\\d{1,2}|X|Y))(\\s)(\\d+)((\\s)((\\w+)(\\s)([+-]))?)?.*");

        try (Stream<String> lines = Files.lines(path)) {

            Iterator it = lines.iterator();
            String delim = "";

            while (it.hasNext()) {

                String line = (String) it.next();
                Matcher line_matcher = entry.matcher(line);

                if (line_matcher.matches()) {

                    if(delim.length() == 0) {
                        if ((line_matcher.group(3).equals("\t") || (line_matcher.group(3).equals(" "))
                                && line_matcher.group(5).equals(line_matcher.group(6))
                                && line_matcher.group(2).equals(line_matcher.group(6))))
                            delim = line_matcher.group(3);
                        else
                            throw new RuntimeException("Unknown delimiter in UserData.java while loading the user file. Please use a tab or space as delimiter between chr1 and the position number in your file");
                    }

                    String[] parts = line.split(delim);

                    if(parts.length > 5) strand.add(parts[7].charAt(0));
                    positions.add(Long.parseLong(line_matcher.group(4)) + chrSizes.offset(assembly, line_matcher.group(1)));
                }
            }

            lines.close();

        } catch (IOException e) {
            System.err.println("In file" + path.toString());
            e.printStackTrace();

        }

        Collections.sort(positions);
    }

    @Override
    public void addPositions(Collection<Long> values) {
        this.positions.addAll(values);
    }

    @Override
    public List<Long> getPositions() {
        return this.positions;
    }

    @Override
    public void setPositions(List<Long> positions) {
        this.positions = positions;
    }

    @Override
    public int getPositionCount() {
        return this.positions.size();
    }

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }

    public List<Character> getStrands() {
        return this.strand;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
