package de.thm.misc;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by menzel on 11/9/16.
 */
public class LogoCreatorTest {
    @Test
    public void createLogo() throws Exception {
        List<String> sequences = new ArrayList<>();

        sequences.add("AATT");
        sequences.add("AACT");
        sequences.add("AATT");
        sequences.add("AATT");

        LogoCreator.createLogo(sequences);

    }


}