package de.thm.backgroundModel;

import de.thm.calc.IntersectCalculate;
import de.thm.calc.IntersectResult;
import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Michael Menzel on 6/1/16.
 */
public class BetterBackgroundModel extends BackgroundModel{

    private Random rand;
    private final int factor = 10;

    public BetterBackgroundModel(){}


    public BetterBackgroundModel(Interval interval,Sites sites) {

        IntersectCalculate calc = new IntersectCalculate();
        IntersectResult result = calc.searchSingleInterval(interval,sites);

        if(interval.getType().equals(Interval.Type.score))
            positions.addAll(randPositionsScored(interval, result));

        else {
            positions.addAll(randPositions(result.getIn()* factor, interval, "in"));
            positions.addAll(randPositions(result.getOut()* factor, interval, "out"));
        }

    }

    private Collection<Long> randPositionsScored(Interval interval, IntersectResult result){

        List<Long> newSites;
        Interval probabilityInterval = interval.copy();

        probabilityInterval.setIntervalScore(generateProbabilityScores(interval,result));

        newSites = generatePositons(probabilityInterval, result.getIn());
        Collections.sort(newSites);

        newSites.addAll(randPositions(result.getOut(), interval, "out"));

        return newSites;
    }

    private List<Long> generatePositons(Interval probabilityInterval, int siteCount) {

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

        for (Double aRandom : random) {
            double value = aRandom - prev;

            for (int j = 0; j < starts.size(); j++) {

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

        for(int i = 0 ; i < scores.size(); i++){
            double prob = count(scores.get(i),result.getResultScores())/result.getResultScores().size();
            Long length = ends.get(i) - starts.get(i);
            probabilityScores.add(prob*length);
        }

        //map values to be 1 if summed up
        double sum = probabilityScores.stream().mapToDouble(Double::doubleValue).sum();
        probabilityScores = probabilityScores.stream().map(p -> p/sum).collect(Collectors.toList());

        return probabilityScores;
    }

    private double count(Double value, List<Double> scores) {

        //TODO dynamic programming

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
        long maxValue = sumOfIntervals(interval, mode);

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

    /**
     * Sums up the size of all intervals. Either all intervals or the space between them
     *
     * @param interval - intervals to sum up
     * @param mode - either "in" or "out".
     *
     * @return sum of interval length inside or outside the intervals
     */
    private long sumOfIntervals(Interval interval, String mode) {

        long size = 0;
        int io = (mode.equals("in"))? 0: 1;

        List<Long> intervalStart = interval.getIntervalsStart();
        List<Long> intervalEnd = interval.getIntervalsEnd();

        for(int i = 0; i < intervalStart.size()-io; i++){
            if(mode.equals("in"))
                size += intervalEnd.get(i) - intervalStart.get(i);
            else
                size +=  intervalStart.get(i+1) - intervalEnd.get(i);
        }

        return size;
    }
}
