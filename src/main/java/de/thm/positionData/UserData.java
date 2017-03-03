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

    /**
     * Constructor
     *
     * @param path - file to load positions from
     */
    public UserData(GenomeFactory.Assembly assembly, Path path) {
        this.assembly = assembly;
        loadPositionsFromFile(path);
    }

    /**
     * Loads positions from a bed file
     *
     * @param path - file to load
     */
    private void loadPositionsFromFile(Path path) {

        ChromosomSizes chrSizes = ChromosomSizes.getInstance();

        Pattern entry = Pattern.compile("(chr(\\d{1,2}|X|Y))\\s(\\d*)\\s((\\w+)\\s([+-]))?.*");

        try (Stream<String> lines = Files.lines(path)) {

            Iterator it = lines.iterator();

            while (it.hasNext()) {

                String line = (String) it.next();
                Matcher line_matcher = entry.matcher(line);

                if (line_matcher.matches()) {
                    String[] parts = line.split("\t");

                    if(parts.length >= 5) strand.add(parts[5].charAt(0));

                    positions.add(Long.parseLong(line_matcher.group(3)) + chrSizes.offset(assembly, line_matcher.group(1)));

                }
            }

            lines.close();

        } catch (IOException e) {
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
}
