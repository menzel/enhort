// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.positionData;

import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
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

    private String filename;
    private transient final Logger logger = LoggerFactory.getLogger(UserData.class);

    private final List<Long> positions;
    private final Genome.Assembly assembly;
    private final List<Character> strands = new ArrayList<>();
    private final String cellline;


    public UserData(Genome.Assembly assembly, List<Long> positions, String cellline, String filename) {
        this.assembly = assembly;
        this.positions = positions;
        this.cellline = cellline;
        this.filename = filename;
    }


    public UserData(Genome.Assembly assembly, List<Long> positions, String cellline) {
        this.assembly = assembly;
        this.positions = positions;
        this.cellline = cellline;
        this.filename = "";
    }

    /**
     * Constructor
     *
     * @param path - file to load positions from
     * @param cellline - cellline
     */
    public UserData(Genome.Assembly assembly, Path path, String cellline) {
        this.assembly = assembly;
        this.cellline = cellline;
        this.positions = new ArrayList<>();

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
        Pattern entry = Pattern.compile("(chr(\\d{1,2}|X|Y))\\s(\\d+)(\\s\\w+)*\\s([\\+\\-])?.*");

        try (Stream<String> lines = Files.lines(path)) {

            Iterator it = lines.iterator();

            while (it.hasNext()) {

                String line = (String) it.next();
                Matcher line_matcher = entry.matcher(line);

                if (line_matcher.matches()) {

                    if (line_matcher.group(5) != null)
                        strands.add(line_matcher.group(5).charAt(0));

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

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
        // do nothing
    }

    @Override
    public List<Character> getStrands() {
        return this.strands;
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
        if (cellline == null)
            return "Unknown";
        return this.cellline;
    }
}
