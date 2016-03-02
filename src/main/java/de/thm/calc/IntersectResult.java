package de.thm.calc;

import de.thm.genomeData.Track;

import java.util.List;
import java.util.Map;

/**
 * Results of one intersect run with the count of inside values. And depening on the interval type result names and result scores.
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
public final class IntersectResult {


    private final Map<String, Integer> resultNames;
    private final List<Double> resultScores;
    private final Track usedInterval;
    private final int in;
    private final int out;


    /**
     * Constructor
     */
    IntersectResult(Track usedInterval, int in, int out) {
        this.usedInterval = usedInterval;
        this.in = in;
        this.out = out;
        this.resultNames = null;
        this.resultScores = null;
    }

    /**
     * Constructor
     */
    IntersectResult(Track usedInterval, int in, int out, Map<String, Integer> names) {
        this.usedInterval = usedInterval;
        this.in = in;
        this.out = out;
        this.resultNames= names;
        this.resultScores = null;
    }

    /**
     * Constructor
     */
    IntersectResult(Track usedInterval, int in, int out, List<Double> scores) {
        this.usedInterval = usedInterval;
        this.in = in;
        this.out = out;
        this.resultNames = null;
        this.resultScores = scores;
    }


    public Map<String, Integer> getResultNames() {
        return resultNames;
    }


    public List<Double> getResultScores() {
        return resultScores;
    }

    @Override
    public String toString() {
        return usedInterval.getName() + "\t" + in + "\t" + out;
    }

    public int getIn() {
        return in;
    }

    public int getOut() { return this.out; }

    public Class getType() {
        return getClass();
    }
}

