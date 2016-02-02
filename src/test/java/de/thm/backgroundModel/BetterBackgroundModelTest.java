package de.thm.backgroundModel;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Michael Menzel on 1/2/16.
 */
public class BetterBackgroundModelTest {

    @Test
    public void testRandPositionsScored() throws Exception {
        ArrayList<Long> startList = new ArrayList<>();
        ArrayList<Long> endList = new ArrayList<>();
        ArrayList<Double> scoreList= new ArrayList<>();
        ArrayList<String> namesList = new ArrayList<>();
        Interval intv = new Interval();
        intv.setType(Interval.Type.score);

        startList.add(5L);
        startList.add(20L);
        startList.add(50L);
        startList.add(80L);

        endList.add(15L);
        endList.add(30L);
        endList.add(80L);
        endList.add(90L);

        scoreList.add(100.0);
        scoreList.add(500.0);
        scoreList.add(50.0);
        scoreList.add(90.0);

        namesList.add("first");
        namesList.add("second");
        namesList.add("third");
        namesList.add("fourth");


        intv.setIntervalsStart(startList);
        intv.setIntervalsEnd(endList);
        intv.setIntervalScore(scoreList);
        intv.setIntervalName(namesList);



        Sites sites = new Sites() {
            @Override
            public void addPositions(Collection<Long> values) {
                super.addPositions(values);
            }
        };


        List<Long> positions = new ArrayList<>();

        positions.add(10L);
        positions.add(12L);
        positions.add(22L);
        positions.add(60L);
        positions.add(70L);

        sites.setPositions(positions);

        SingleTrackBackgroundModel better = new SingleTrackBackgroundModel(intv, sites);


    }
}