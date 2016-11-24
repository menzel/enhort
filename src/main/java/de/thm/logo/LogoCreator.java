package de.thm.logo;

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

    /**
     * Creates a List of Map of <String, Double> in which the String is a base (a,t,c,g) the Double is the height
     * and each map of the list corresponds to a positions in the given sequences
     *
     * @param sequences - sequence to create logo from. Should all be of the same length
     *
     * @return heights for a logo
     */
    public static Logo createLogo(List<String> sequences){
        Logo logo = new Logo();

        for(int i = 0; i < sequences.get(0).length(); i++) {

            int finalI = i;
            String bases = sequences.stream().map(s -> s.substring(finalI, finalI+1)).collect(Collectors.joining());

            //information content:
            logo.add(getHeights(bases));
        }

        return logo;

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
