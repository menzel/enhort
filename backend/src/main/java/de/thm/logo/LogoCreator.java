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
package de.thm.logo;

import de.thm.positionData.Sites;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.math3.util.FastMath.log;

/**
 * Creates a list of maps of heights for a sequence logo for a given list of sequences
 *
 * Created by menzel on 11/8/16.
 */
public final class LogoCreator {

    private static final Logger logger = LoggerFactory.getLogger(LogoCreator.class);

    public static Logo createLogo(Sites sites){
        return createLogo(GenomeFactory.getInstance().getSequence(sites.getAssembly(), sites, 12, 3000));
    }

    /**
     * Creates a List of Map of <String, Double> in which the String is a base (a,t,c,g) the Double is the height
     * and each map of the list corresponds to a positions in the given sequences *
     *
     * @param sequences - sequence to create logo from. Should all be of the same length!
     *
     * @return heights for a logo
     */
    public static Logo createLogo(List<String> sequences){
        Sequencelogo sequencelogo = new Sequencelogo();
        int l = sequences.get(0).length();

        if(sequences.stream().filter(s -> s.length() != l).count() > 0) {
            logger.warn("LogoCreator: The sequences do not have the same length:");
            logger.warn("They should be " + sequences.get(0).length());
            logger.warn("And some are: " + Arrays.toString(sequences.stream().filter(s -> s.length() != l).toArray()));
        }

        for(int i = 0; i < l; i++) {

            int finalI = i;
            String bases = sequences.stream()
                            .map(s -> s.substring(finalI, finalI+1))
                            .collect(Collectors.joining());

            //information content:
            sequencelogo.add(getHeights(bases));
        }

        return sequencelogo;

    }

    private static Map<String, Double> getHeights(String bases) {
        int a = 0, t = 0 , c = 0 , g = 0;

        Map<String, Double> heights = new HashMap<>();
        bases = bases.toLowerCase();

        for(int i = 0; i < bases.length();i++){
            char s = bases.charAt(i);

            if (s == 'a') a++;
            else if (s == 't') t++;
            else if (s == 'c') c++;
            else if (s == 'g') g++;
        }

        double size = bases.length();

        double H_i = 0;
        if(a > 0) H_i += a/size * log(2,a/size);
        if(t > 0) H_i += t/size * log(2,t/size);
        if(c > 0) H_i += c/size * log(2,c/size);
        if(g > 0) H_i += g/size * log(2,g/size);
        H_i *= -1;

        double R_i = log(2,4) - H_i;

        heights.put("a", (a/size) * R_i);
        heights.put("t", (t/size) * R_i);
        heights.put("c", (c/size) * R_i);
        heights.put("g", (g/size) * R_i);

        return heights;
    }

}
