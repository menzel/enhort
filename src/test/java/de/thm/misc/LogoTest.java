package de.thm.misc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by menzel on 11/8/16.
 */
public class LogoTest {
    @Test
    public void createLogo() throws Exception {
        List<String> sequences = new ArrayList<>();

        sequences.add("AATT");
        sequences.add("AACT");
        sequences.add("AATT");
        sequences.add("AATT");

        Logo.createLogo(sequences);

    }

}