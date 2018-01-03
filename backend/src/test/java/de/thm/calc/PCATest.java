package de.thm.calc;

import de.thm.genomeData.tracks.InOutTrack;
import de.thm.result.ResultCollector;
import de.thm.stat.TestResult;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;

public class PCATest {
    @Test
    public void createBedPCA() throws Exception {

        ResultCollector collector = new ResultCollector(null, null, Collections.emptyList());

        for (int i = 0; i < 31; i++) {

            double x = i * Math.PI;
            InOutTrack tmp = mock(InOutTrack.class);
            TestResult ts = new TestResult(0, new TestTrackResult(tmp, 0, 1), new TestTrackResult(tmp, 1, 0), x, tmp);
            collector.addResult(ts);
        }

        PCA pca = new PCA();
        pca.createBedPCA(collector);

        //TODO assert

    }

}