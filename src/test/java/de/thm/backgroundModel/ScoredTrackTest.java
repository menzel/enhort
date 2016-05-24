package de.thm.backgroundModel;

import de.thm.genomeData.TrackFactory;
import de.thm.genomeData.Tracks;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by menzel on 5/20/16.
 */
public class ScoredTrackTest {
     @Test
    public void bin() throws Exception {
        List<Double> vals = new ArrayList<>();

        vals.add(0.11);
        vals.add(0.19);
        vals.add(0.22);
        vals.add(0.23);

        vals.add(0.41);
        vals.add(0.42);
        vals.add(0.431);

        vals.add(0.51);
        vals.add(0.52);
        vals.add(0.53);

        vals.add(0.89);
        vals.add(0.91);
        vals.add(0.934);
        vals.add(0.95);

        vals.add(0.999);


        Tracks.bin(TrackFactory.getInstance().createScoredTrack(null,null,null,vals,null,null), 5);


        List<Double> expected = new ArrayList<>();

        /*
        //result list:
        "0.222"
        "0.222"
        "0.222"
        "0.4244"
        "0.4244"
        "0.4244"
        "0.526"
        "0.526"
        "0.526"
        "0.9292"
         "0.9292"
         "0.9292"
         "0.999"
         "0.999"
         "0.999"
         */
        //assertEquals(expected,binned);
    }


}