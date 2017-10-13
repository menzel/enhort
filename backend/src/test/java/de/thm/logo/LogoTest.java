package de.thm.logo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by menzel on 11/24/16.
 */
public class LogoTest {
    @Test
    public void getConsensus() throws Exception {

        List<String> sequences = new ArrayList<>();

        sequences.add("AATT");
        sequences.add("AACT");
        sequences.add("AATT");
        sequences.add("AATT");
        sequences.add("AATG");


        assertEquals("AATT", LogoCreator.createLogo(sequences).getConsensus());


    }

}