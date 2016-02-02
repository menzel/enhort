package de.thm.positionData;

import de.thm.genomeData.Interval;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Michael Menzel on 8/1/16.
 */
public class BetterBackgroundModelTest {

    @Test
    public void testConstructor() throws Exception{
        //BetterBackgroundModel better = new BetterBackgroundModel(mockInterval(4));
        throw new Exception("No test here");

    }

    private static Interval mockInterval(int i) {
        Interval intv = new Interval() {
            @Override
            protected void handleParts(String[] parts) { }

            @Override
            public ArrayList<Long> getIntervalsEnd() {
                return new ArrayList<Long>(){{
                    add(20L);
                    add(28L);
                    add(50L);
                }};
            }

            @Override
            public ArrayList<Long> getIntervalsStart() {
                return new ArrayList<Long>(){{
                    add(17L);
                    add(22L);
                    add(30L);
                }};
            }

        };

        return intv;
    }


}