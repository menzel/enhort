package de.thm.teststubs;

import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class UserData implements Sites {

    private final Genome.Assembly assembly;
    private List<Long> positions = new ArrayList<>();
    private List<Character> strand = new ArrayList<>();
    private String filename;

    private final Logger logger = LoggerFactory.getLogger(UserData.class);

    /**
     * Constructor
     *
     * @param path - file to load positions from
     */
    public UserData(Genome.Assembly assembly, Path path) {
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
        Pattern entry = Pattern.compile("(chr(\\d{1,2}|X|Y))\\s(\\d+)(\\s((\\w+)\\s([+-]))?)?.*");

        try (Stream<String> lines = Files.lines(path)) {

            Iterator it = lines.iterator();

            while (it.hasNext()) {

                String line = (String) it.next();
                Matcher line_matcher = entry.matcher(line);

                if (line_matcher.matches()) {

                    if(line_matcher.group(7) != null)
                        strand.add(line_matcher.group(7).charAt(0));

                    positions.add(Long.parseLong(line_matcher.group(3)) + chrSizes.offset(assembly, line_matcher.group(1)));
                }
            }

            lines.close();

        } catch (IOException e) {
            logger.warn("In file" + path.toString());
            logger.error("Exception {}", e.getMessage(), e);

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
    public Genome.Assembly getAssembly() {
        return this.assembly;
    }

    @Override
    public String getCellline() {
        return "Unknown";
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
