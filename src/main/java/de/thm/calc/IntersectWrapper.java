package de.thm.calc;

import de.thm.genomeData.Track;
import de.thm.positionData.Sites;
import de.thm.stat.EffectSize;
import de.thm.stat.IndependenceTest;
import de.thm.stat.ResultCollector;
import de.thm.stat.TestResult;

/**
 * Wraps the call to one intersect round. A round consists of one interval (track), one set of positions from the outside
 * and one set of positions made up by a background model.
 *
 * It sets the test result of the run to a list in the given collector.
 *
 * Created by Michael Menzel on 12/1/16.
 */
class IntersectWrapper<T extends Track> implements Runnable{


    private final Sites randomPos;
    private final Sites measuredPos;
    private final Track track;
    private final ResultCollector collector;

    /**
     * Constructor for the wrapper object
     *
     * @param measuredPos - positions from the outside of the program
     * @param randomPos - positions to match against made up by a background model
     * @param track - interval to match against
     * @param collector - collector to collect results in
     */
    IntersectWrapper(Sites measuredPos, Sites randomPos, Track track, ResultCollector collector) {

        this.randomPos = randomPos;
        this.measuredPos = measuredPos;
        this.track = track;
        this.collector = collector;
    }

    @Override
    public void run() {
        Intersect<T> intersec1 = new IntersectCalculate<>();
        Intersect<T> intersec2 = new IntersectCalculate<>();

        IntersectResult result1 = intersec1.searchSingleInterval((T) track, measuredPos);
        IntersectResult result2 = intersec2.searchSingleInterval((T) track, randomPos);

        IndependenceTest<T> tester = new IndependenceTest<>();
        EffectSize effectSize = new EffectSize();

        TestResult testResult  = tester.test(result1, result2, track);
        effectSize.test(result1, result2);

        collector.addResult(testResult);

    }
}
