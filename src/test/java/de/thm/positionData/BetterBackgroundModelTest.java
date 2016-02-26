package de.thm.positionData;

import de.thm.genomeData.Track;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 8/1/16.
 */
public class BetterBackgroundModelTest {

    @Test
    public void testConstructor() throws Exception{
        //BetterBackgroundModel better = new BetterBackgroundModel(mockInterval(4));
        throw new Exception("No test here");

    }

    private static Track mockInterval(int i) {
        Track intv = new Track() {

            @Override
            public ArrayList<Long> getIntervalsEnd() {
                return new ArrayList<Long>(){{
                    add(20L);
                    add(28L);
                    add(50L);
                }};
            }

            @Override
            public List<Double> getIntervalScore() {
                return null;
            }

            @Override
            public int getUid() {
                return 0;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getFilename() {
                return null;
            }

            @Override
            public Track clone() {
                return null;
            }

            @Override
            public Track.Type getType() {
                return null;
            }

            @Override
            public List<String> getIntervalName() {
                return null;
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