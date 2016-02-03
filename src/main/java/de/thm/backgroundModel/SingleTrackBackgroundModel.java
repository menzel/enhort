package de.thm.backgroundModel;

import de.thm.calc.IntersectCalculate;
import de.thm.calc.IntersectResult;
import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.positionData.Sites;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements the background model sites generation for a single track as covariance.
 * Scored or inout intervals are possible.
 *
 * Created by Michael Menzel on 6/1/16.
 */
public class SingleTrackBackgroundModel extends BackgroundModel{

    private Random rand;
    private final int factor = 10;

    /**
     * Contstructor
     */
    public SingleTrackBackgroundModel(){}


    /**
     * Constructor for running sites against one interval
     *
     * @param interval - interval to search against
     * @param sites - sites to search
     */
    public SingleTrackBackgroundModel(Interval interval, Sites sites) throws Exception {

        IntersectCalculate calc = new IntersectCalculate();
        IntersectResult result = calc.searchSingleInterval(interval,sites);

        if(interval.getType().equals(Interval.Type.score)) {
            positions.addAll(randPositionsScored(interval, result));
        } else {
            positions.addAll(randPositions(result.getIn()* factor, interval, "in"));
            positions.addAll(randPositions(result.getOut()* factor, interval, "out"));
        }

    }

    /**
     * Generate positions for scored interval
     *
     * @param interval - interval of type score
     * @param result intersect result of the interval and some sites
     *
     * @return collection of positions according to interval
     */
    private Collection<Long> randPositionsScored(Interval interval, IntersectResult result) throws Exception {

        if(interval.getType()!= Interval.Type.score)
            throw new Exception("Wrong type");

        List<Long> newSites = new ArrayList<>();
        Interval probabilityInterval = interval.copy();

        probabilityInterval.setIntervalScore(generateProbabilityScores(interval,result));

        newSites.addAll(generatePositonsByProbability(probabilityInterval, result.getIn()));
        Collections.sort(newSites);

        newSites.addAll(randPositions(result.getOut(), interval, "out"));

        return newSites;
    }

    /**
     * generates positions inside the interval according to the probabilities in the probability interval.
     *
     *
     * @param probabilityInterval - interval with probability as score
     * @param siteCount - count of sites to be generated inside
     *
     * @return collection of positions inside the interval
     */
    private Collection<Long> generatePositonsByProbability(Interval probabilityInterval, int siteCount) {

        List<Long> sites = new ArrayList<>();
        List<Long> starts = probabilityInterval.getIntervalsStart();
        List<Long> ends = probabilityInterval.getIntervalsEnd();
        List<Double> probabilities = probabilityInterval.getIntervalScore();
        List<Double> random = new ArrayList<>();
        rand = new Random(System.currentTimeMillis());

        for(int i = 0; i < siteCount; i++){
            random.add(rand.nextDouble());
        }

        Collections.sort(random);


        double prev = 0;
        int j = 0;

        for (Double aRandom : random) {
            double value = aRandom - prev;

            for (; j < starts.size(); j++) {

                double prob = probabilities.get(j);

                if (value > prob) {
                    value -= prob;
                    prev += prob;

                } else {
                    Long intervalLength = ends.get(j) - starts.get(j);
                    sites.add(starts.get(j) + Math.round(intervalLength * value));

                    break;
                }
            }
        }

        return sites;
    }


    /**
     * Translate scores into probability for each interval based on frequency of sites inside.
     * A smoothing function is applied to the probability values.
     *
     * @param interval - intervals with scores
     * @param result - result of intersecting the interval with the given user input
     *
     * @return new score list to be added to a new interval with probability scores
     */
    private List<Double> generateProbabilityScores(Interval interval, IntersectResult result) {

        List<Double> scores = interval.getIntervalScore();
        List<Double> probabilityScores = new ArrayList<>();
        List<Long> starts = interval.getIntervalsStart();
        List<Long> ends = interval.getIntervalsEnd();

        Map<Double, Double> probValues = new HashMap<>(); // for dynamic programing

        for(int i = 0 ; i < scores.size(); i++){

            double prob;

            if(probValues.containsKey(scores.get(i))) {
                prob = probValues.get(scores.get(i));
            } else {
                prob = count(scores.get(i), result.getResultScores()) / result.getResultScores().size();
                probValues.put(scores.get(i), prob);
            }
            Long length = ends.get(i) - starts.get(i);
            //probabilityScores.add(prob*Math.log(length));
            probabilityScores.add(prob); //TODO how to use length
        }

        //map values to be 1 if summed up
        double sum = probabilityScores.stream().mapToDouble(Double::doubleValue).sum();
        probabilityScores = probabilityScores.stream().map(p -> p/sum).collect(Collectors.toList());

        return probabilityScores;
    }

    private double count(Double value, List<Double> scores) {

        int count = 0;

        for (int i = 0, scoresSize = scores.size(); i < scoresSize; i++) {
            Double score = scores.get(i);
            if (score.equals(value)) {
                count++;
            }
        }

        return count;
}


    /**
     * Generates random positions which are either all inside or outside of the given intervals
     *
     * @param siteCount - count of random positions to be made up
     * @param interval - interval by which the in/out check is made
     * @param mode - either the string "in" or "out". Controls the behavior of setting the rand positions in or outside of the intervals
     *
     * @return Collection of random positions
     */
    public Collection<Long> randPositions(int siteCount, Interval interval, String mode) {

        int io = (mode.equals("in"))? 0: 1; //remember if rand positions should be in or outside of an interval

        rand = new Random(System.currentTimeMillis());
        long maxValue = Intervals.sumOfIntervals(interval, mode);

        List<Long> randomValues = new ArrayList<>();
        List<Long> sites = new ArrayList<>();
        List<Long> intervalStart = interval.getIntervalsStart();
        List<Long> intervalEnd = interval.getIntervalsEnd();

        //get some random numbers
        for(int i = 0; i < siteCount; i++){
            Long r = Math.round(Math.floor(rand.nextDouble() * (maxValue)));
            randomValues.add(r);
        }

        Collections.sort(randomValues); // very important!

        //strech random values to whole genome:
        int j = 0;
        long sumOfPrevious = 0; // remember sum of previous intervals.

        for(int i = 0; i < siteCount; i++){
            long iStart = intervalStart.get(j + io); // io is 0 when the rand position should be inside an interval, 1 otherwise
            long iEnd = intervalEnd.get(j);
            long randV = randomValues.get(i) - sumOfPrevious; // substract sum of previous intervals. Since random values are in order this works.

            while(iEnd-1 < iStart + randV && j < intervalStart.size()-(1+io)){ // if it does not fit in go to next interval and substract interval length from rand value
                j++;
                randV = randV  - (iEnd - iStart);
                sumOfPrevious += (iEnd - iStart);
                iStart = intervalStart.get(j + io);
                iEnd = intervalEnd.get(j);
            }

            if(io == 0) //if position should be inside the interval
                sites.add(iStart + randV);
            else //otherwise
                sites.add(iEnd + randV);
        }

        Collections.sort(sites); //don't forget this

        return sites;

    }

}
