package de.thm.backgroundModel;

import de.thm.genomeData.Track;
import de.thm.genomeData.IntervalFactory;
import de.thm.genomeData.Intervals;
import de.thm.genomeData.ScoredTrack;
import de.thm.positionData.Sites;

import java.util.*;

/**
 * Background model for multiple tracks which have scored values.
 *
 * Created by Michael Menzel on 17/2/16.
 */
class ScoreMultiTrackBackgroundModel implements Sites{

    private List<Long> positions = new ArrayList<>();

    ScoreMultiTrackBackgroundModel() { }

    /**
     * Consturcotr
     *
     * @param sites - sites to build model against.
     * @param covariants - list of intervals to build model against.
     */
    ScoreMultiTrackBackgroundModel(List<ScoredTrack> covariants, Sites sites) {
        ScoredTrack interval = generateProbabilityInterval(sites, covariants);

        int count = (sites.getPositionCount() > 10000)? sites.getPositionCount() : 12000;
        Collection<Long> pos = generatePositionsByProbability(interval, count);

        positions.addAll(pos);
    }


    /**
     * Generates an interval with probabilities as scores based on the intervals given and the sites.
     * In the names of the interval is the original score combination based on the hashing.
     *
     * @param sites - sites to set probability by.
     * @param intervals - list of intervals as covariants.
     *
     * @return new interval with probability scores.
     */
    ScoredTrack generateProbabilityInterval(Sites sites, List<ScoredTrack> intervals) {

        Map<String, Double> sitesOccurence = fillOccurenceMap(intervals,sites);

        double sum = sites.getPositionCount();
        for(String k: sitesOccurence.keySet())
            sitesOccurence.put(k, sitesOccurence.get(k)/sum);

        ScoredTrack interval = Intervals.combine(intervals, sitesOccurence);

        Map<String, Integer> genomeOccurence = new HashMap<>();

        //count occurences:
        assert interval != null;
        for(String key: interval.getIntervalName()){
            if(genomeOccurence.containsKey(key)){
                genomeOccurence.put(key, genomeOccurence.get(key)+1);
            } else {
                genomeOccurence.put(key, 1);
            }
        }

        //TODO create hash for lengths per key

        List<String> keys = interval.getIntervalName();
        List<Double> intervalScore = interval.getIntervalScore();
        List<Double> newScores = new ArrayList<>();

        for (int i = 0; i < intervalScore.size(); i++) {
            Double p = intervalScore.get(i);
            int o = genomeOccurence.get(keys.get(i));

            if(p == null){
                newScores.add(0d);

            } else {
                p = p / o;
                newScores.add(p);
            }
        }

        //if(newScores.stream().mapToDouble(i->i).sum() < 0.99){ //TODO eval check }

        return IntervalFactory.getInstance().createScoredTrack(
                    interval.getIntervalsStart(),
                    interval.getIntervalsEnd(),
                    interval.getIntervalName(),
                    newScores,
                    interval.getName(),
                    interval.getDescription());
    }

    /**
     * Computes an occurence map which holds information about how often a score combination from the given intervals is picked by one of the given sites.
     * The returning map contains probablities. *
     *
     * @param intervals - scores to get from.
     * @param sites - positions to look up.
     *
     * @return map to score combination to  probablity
     */
    Map<String, Double> fillOccurenceMap(List<ScoredTrack> intervals, Sites sites) {
        Map<String, Double> map = new HashMap<>(); //holds the conversion between score and probability
        Map<Track, Integer> indices = new HashMap<>();

        //init indices map:
        for(Track track : intervals) {
            indices.put(track, 0);
        }

        for(Long p : sites.getPositions()){
            String key = "";

            for(ScoredTrack interval:intervals){

                List<Long> intervalStart = interval.getIntervalsStart();
                List<Long> intervalEnd = interval.getIntervalsEnd();

                int i = indices.get(interval);
                int intervalCount = intervalStart.size()-1;

                while(i < intervalCount && intervalStart.get(i) <= p){
                    i++;
                }

                if(i == 0){
                        key += "|";

                } else if(i == intervalCount && p > intervalEnd.get(i-1)){ //last Interval and p not in previous
                    if(p < intervalEnd.get(i) && p >= intervalStart.get(i)){
                        //inside last interval
                        key += "|" + interval.getIntervalScore().get(i);

                    } else{
                        key += "|";
                    }
                }else{
                    if(p >= intervalEnd.get(i-1)){
                        key += "|"; // not inside the previous interval

                    }else{
                        key += "|" + interval.getIntervalScore().get(i-1);
                    }
                }

                indices.put(interval, i);

            }

            if(map.containsKey(key)){
                map.put(key, map.get(key)+1);
            } else {
                map.put(key, 1.);
            }
        }
        return map;
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
    private Collection<Long> generatePositionsByProbability(ScoredTrack probabilityInterval, int siteCount) {

        List<Long> sites = new ArrayList<>();
        List<Long> starts = probabilityInterval.getIntervalsStart();
        List<Long> ends = probabilityInterval.getIntervalsEnd();
        List<Double> probabilities = probabilityInterval.getIntervalScore();
        List<Double> random = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());

        for(int i = 0; i < siteCount; i++){
            random.add(rand.nextDouble());
        }

        Collections.sort(random);


        double prev = 0;
        int j = 0;

        for (Double aRandom : random) {
            double value = aRandom - prev;

            for (; j < starts.size(); j++) {

                Double prob = probabilities.get(j);

                if (value >= prob) {
                    value -= prob;
                    prev += prob;

                } else {
                    Long intervalLength = (ends.get(j) - starts.get(j)) - 1;
                    sites.add(starts.get(j) + Math.round(intervalLength * value));

                    break;
                }
            }
        }

        Collections.sort(sites);
        return sites;
    }


    @Override
    public void addPositions(Collection<Long> values) {
        this.positions.addAll(values);
    }

    @Override
    public List<Long> getPositions() {
        return this.positions;
    }

    @Override
    public void setPositions(List<Long> positions) {
        this.positions = positions;
    }

    @Override
    public int getPositionCount() {
        return this.positions.size();
    }
}
