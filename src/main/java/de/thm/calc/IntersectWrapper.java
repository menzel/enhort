package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;
import de.thm.stat.IndependenceTest;
import de.thm.stat.TestResult;

/**
 * Created by Michael Menzel on 12/1/16.
 */
public class IntersectWrapper implements Runnable{


    private final Sites randomPos;
    private final Sites measuredPos;
    private final Interval interval;
    private String trackName;
    private TestResult testResult;

    public IntersectWrapper(Sites measuredPos, Sites randomPos, Interval interval, String trackName) {

        this.randomPos = randomPos;
        this.measuredPos = measuredPos;
        this.interval = interval;
        this.trackName = trackName;
    }

    @Override
    public void run() {
        Intersect intersec1 = new IntersectSimple();
        Intersect intersec2 = new IntersectSimple();

        Result result1 = intersec1.searchSingleInterval(interval, measuredPos);
        Result result2 = intersec2.searchSingleInterval(interval, randomPos);

        IndependenceTest tester = new IndependenceTest();
        testResult  = tester.test(result1, result2, trackName);
        System.out.println(testResult);

    }

    public TestResult getTestResult() {
        return testResult;
    }
}
