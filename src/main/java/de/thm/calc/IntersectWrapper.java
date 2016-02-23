package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;
import de.thm.stat.EffectSize;
import de.thm.stat.ResultCollector;
import de.thm.stat.IndependenceTest;
import de.thm.stat.TestResult;

/**
 * Created by Michael Menzel on 12/1/16.
 */
class IntersectWrapper implements Runnable{


    private final Sites randomPos;
    private final Sites measuredPos;
    private final Interval interval;
    private TestResult testResult;
    private ResultCollector collector;

    IntersectWrapper(Sites measuredPos, Sites randomPos, Interval interval, ResultCollector collector) {

        this.randomPos = randomPos;
        this.measuredPos = measuredPos;
        this.interval = interval;
        this.collector = collector;
    }

    @Override
    public void run() {
        Intersect intersec1 = new IntersectCalculate();
        Intersect intersec2 = new IntersectCalculate();

        IntersectResult result1 = intersec1.searchSingleInterval(interval, measuredPos);
        IntersectResult result2 = intersec2.searchSingleInterval(interval, randomPos);

        IndependenceTest tester = new IndependenceTest();
        EffectSize effectSize = new EffectSize();

        testResult  = tester.test(result1, result2, interval);
        effectSize.test(result1, result2);

        collector.addResult(testResult);

    }

    TestResult getTestResult() {
        return testResult;
    }
}
