package de.thm.backgroundModel;

import de.thm.calc.Intersect;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Background model for multiple tracks which have scored values.
 * <p>
 * Created by Michael Menzel on 17/2/16.
 */
class ScoreMultiTrackBackgroundModel implements Sites {

    private List<Long> positions = new ArrayList<>();

    ScoreMultiTrackBackgroundModel() {}


     /**
     * Constructor
     *
     * @param sites      - sites to build model against.
     * @param covariant - single covariant
     */
    ScoreMultiTrackBackgroundModel(ScoredTrack covariant, Sites sites, int minSites) {

        this(Collections.singletonList(covariant),sites, minSites);
    }


    /**
     * Constructor
     *
     *
     * @param sites      - sites to build model against.
     * @param covariants - list of intervals to build model against.
     */
    ScoreMultiTrackBackgroundModel(List<ScoredTrack> covariants, Sites sites, int minSites) {
        ScoredTrack interval = generateProbabilityInterval(sites, covariants);

        int count = (sites.getPositionCount() > minSites) ? sites.getPositionCount() : minSites;
        Collection<Long> pos = generatePositionsByProbability(interval, count);

        Intersect<Track> foo = new Intersect<>();


        positions.addAll(pos);

        int a = foo.searchSingleInterval(covariants.get(0), this).getOut();
        int b = foo.searchSingleInterval(covariants.get(0), sites).getOut();

        //System.out.println("should: " + b + " is: " + a);
    }


    /**
     * Generates an interval with probabilities as scores based on the intervals given and the sites.
     * In the names of the interval is the original score combination based on the hashing.
     *
     * @param sites     - sites to set probability by.
     * @param intervals - list of intervals as covariants.
     * @return new interval with probability scores.
     */
    ScoredTrack generateProbabilityInterval(Sites sites, List<ScoredTrack> intervals) {


        Map<String, Double> sitesOccurence = fillOccurenceMap(intervals, sites);

        double sum = sites.getPositionCount();
        for (String k : sitesOccurence.keySet())
            sitesOccurence.put(k, sitesOccurence.get(k) / sum);

        ScoredTrack interval = combine(intervals, sitesOccurence);
        assert interval != null;


        // Fill occurences maps over whole genome

        List<Long> starts = interval.getIntervalsStart();
        List<Long> ends = interval.getIntervalsEnd();

        Map<String, Long> lengths = new HashMap<>();


        //count occurences over whole genome:

        int j = 0;
        for (String key : interval.getIntervalName()) {
            if (lengths.containsKey(key)) {
                lengths.put(key, lengths.get(key) + ends.get(j) - starts.get(j));
            } else {
                lengths.put(key, ends.get(j) - starts.get(j));
            }
            j++;
        }

        //generate prob list for each interval

        List<String> keys = interval.getIntervalName();
        List<Double> intervalScore = interval.getIntervalScore();
        List<Double> newScores = new ArrayList<>();

        for (int i = 0; i < intervalScore.size(); i++) {
            Double p = intervalScore.get(i);

            if (p == null) {
                newScores.add(0d);

            } else {

                double genomeLength = lengths.get(keys.get(i));
                double prob = 0;
                double length = ends.get(i) - starts.get(i);

                if(genomeLength != 0) { //this can happen with multiple scored tracks
                    prob = p * (length/genomeLength);

                    newScores.add(prob);
                }
            }
        }


        //strech values to sum up to 1.0 if inaccuracy caused smaller prob values

        double exp = newScores.stream().mapToDouble(i->i).sum();
        if(exp < (1 - 0.00000000001)){ //if the combined probability is below 1.0 increase each value:
            double inc = 1 / exp;
            newScores = newScores.stream().map(i -> i * inc).collect(Collectors.toList());
        }


        // create scored track from the generated prob values


        return TrackFactory.getInstance().createScoredTrack(
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
     * @param sites     - positions to look up.
     * @return map to score combination to  probablity
     */
    Map<String, Double> fillOccurenceMap(List<ScoredTrack> intervals, Sites sites) {
        Map<String, Double> map = new HashMap<>(); //holds the conversion between score and probability
        Map<Track, Integer> indices = new HashMap<>();

        //init indices map:
        for (Track track : intervals) {
            indices.put(track, 0);
        }

        for (Long p : sites.getPositions()) {
            String key = "";

            for (ScoredTrack interval : intervals) {

                List<Long> intervalStart = interval.getIntervalsStart();
                List<Long> intervalEnd = interval.getIntervalsEnd();

                int i = indices.get(interval);
                int intervalCount = intervalStart.size() - 1;

                while (i < intervalCount && intervalStart.get(i) <= p) {
                    i++;
                }

                if (i == 0) {
                    key += "|";

                } else if (i == intervalCount && p > intervalEnd.get(i - 1)) { //last Interval and p not in previous
                    if (p < intervalEnd.get(i) && p >= intervalStart.get(i)) {
                        //inside last interval
                        key += "|" + interval.getIntervalScore().get(i);

                    } else {
                        key += "|";
                    }
                } else {
                    if (p >= intervalEnd.get(i - 1)) {
                        key += "|"; // not inside the previous interval

                    } else {
                        key += "|" + interval.getIntervalScore().get(i - 1);
                    }
                }

                indices.put(interval, i);

            }

            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1.);
            }
        }
        return map;
    }


    /**
     * generates positions inside the interval according to the probabilities in the probability interval.
     *
     * @param probabilityInterval - interval with probability as score
     * @param siteCount           - count of sites to be generated inside
     * @return collection of positions inside the interval
     */
    Collection<Long> generatePositionsByProbability(ScoredTrack probabilityInterval, int siteCount) {

        List<Long> sites = new ArrayList<>();
        List<Long> starts = probabilityInterval.getIntervalsStart();
        List<Long> ends = probabilityInterval.getIntervalsEnd();
        List<Double> probabilities = probabilityInterval.getIntervalScore();
        List<Double> random = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());

        //generate random numbers
        for (int i = 0; i < siteCount; i++) {
            random.add(rand.nextDouble());
        }

        Collections.sort(random);

        //set random values across the track
        double prev = 0;
        int j = 0;

        for (Double aRandom : random) {
            double currentRandom = aRandom - prev;

            for (; j < starts.size(); j++) {

                double prob = probabilities.get(j);

                if (currentRandom >= prob) { // current random value does not fit inside interval
                    currentRandom -= prob;
                    prev += prob;

                } else { // current random value fits inside interval
                    Long intervalLength = (ends.get(j) - starts.get(j));

                    Long position = starts.get(j) + Math.round(Math.floor(intervalLength * currentRandom / prob));
                    sites.add(position);

                    break; //  break for loop and get next random value
                }
            }
        }

        Collections.sort(sites);
        return sites;
    }


    /**
     * Combines a list of intervals to a probability interval.
     * Probablities are given in a map with (k,v): (score, probability)
     * <p>
     * The intervals between the given intervals are also filled with scores.
     *
     * @param intervals - list of intervals
     * @param score_map - map of score names to probabilities. The score names should match the scores in intv1 and intv2
     * @return Interval of type GenomeInterval
     */
    private ScoredTrack combine(List<ScoredTrack> intervals, Map<String, Double> score_map) {
        if (intervals.size() == 0) {
            return null;

        } else if (intervals.size() == 1) {
            return combine(intervals.get(0), score_map);

        } else if (intervals.size() == 2) {
            return combine(intervals.get(0), intervals.get(1), score_map);

        } else {
            List<ScoredTrack> newList = new ArrayList<>();
            newList.addAll(intervals.subList(2, intervals.size()));

            newList.add(combine(intervals.get(0), intervals.get(1), score_map));

            return combine(newList, score_map);
        }

    }

    private ScoredTrack combine(ScoredTrack inputInterval, Map<String, Double> score_map) {

        Map<String, Double> newMap = new HashMap<>(score_map.size());

        //convert score map to have values for dual interval list
        score_map.keySet().stream().forEach(key -> {
            double value = score_map.get(key);
            newMap.put(key.concat("|"), value);
        });

        //do default combine
        ScoredTrack emptyTrack = TrackFactory.getInstance().createScoredTrack(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), "", "");
        return combine(inputInterval, emptyTrack, newMap);
    }

    /**
     * Combines two intervals to a probability interval.
     * Probablities are given in a map with (k,v): (score, probability)
     * <p>
     * The intervals between the given intervals are also filled with scores.
     *
     * @param intv1     - first interval to combine
     * @param intv2     - second interval to combine
     * @param score_map - map of score names to probabilities. The score names should match the scores in intv1 and intv2
     * @return Interval of type GenomeInterval
     */
    private ScoredTrack combine(ScoredTrack intv1, ScoredTrack intv2, Map<String, Double> score_map) {

        List<Long> starts1 = intv1.getIntervalsStart();
        List<Long> starts2 = intv2.getIntervalsStart();

        List<Long> ends1 = intv1.getIntervalsEnd();
        List<Long> ends2 = intv2.getIntervalsEnd();

        List<Double> scores1 = intv1.getIntervalScore();
        List<Double> scores2 = intv2.getIntervalScore();

        List<Long> result_start = new ArrayList<>();
        List<Long> result_end = new ArrayList<>();
        List<Double> result_score = new ArrayList<>();
        List<String> result_names = new ArrayList<>();

       long genomeSize = ChromosomSizes.getInstance().getGenomeSize();


        int i2 = 0;
        int i1 = 0;

        //if (intv1.getIntervalsStart().get(0) != 0L && intv2.getIntervalsStart().get(0) != 0L) result_start.add(0L);

        while (i1 < starts1.size()) {
            /* Abandon hope all ye who enter here */

            while (true) {
                long s1 = genomeSize;
                long e1 = genomeSize;

                long e2 = genomeSize;
                long s2 = genomeSize;


                if (i1 < starts1.size()) {
                    s1 = starts1.get(i1);
                    e1 = ends1.get(i1);
                }

                if (i2 < starts2.size()) {
                    e2 = ends2.get(i2);
                    s2 = starts2.get(i2);
                }


                if (s1 == genomeSize && s1 == s2 && e1 == genomeSize && e1 == e2) {
                    //break out of both loops because this is the last iteration. both intervals are equals genome size
                    i1 = Integer.MAX_VALUE;
                    break;
                }

                result_score.add(score_map.get("||"));
                result_names.add("||");

                if (s1 < s2 && e1 <= s2) {// no overlap, interval from 1 is next

                    result_end.add(s1);

                    result_start.add(s1);
                    result_end.add(e1);

                    result_start.add(e1);

                    String ref = "|".concat(scores1.get(i1).toString()).concat("|");
                    result_score.add(score_map.get(ref));
                    result_names.add(ref);

                    i1++;
                } else if (s1 > s2 && e2 <= s1) { //no overlap, interval from 2 comes first

                    result_end.add(s2);

                    result_start.add(s2);
                    result_end.add(e2);

                    result_start.add(e2);

                    String ref = "||".concat(scores2.get(i2).toString());
                    result_score.add(score_map.get(ref));
                    result_names.add(ref);

                    i2++;
                } else if ((s1 < e2 && e1 > e2) || s2 < e1 && e2 > e1) { //overlap

                    //outside part
                    result_end.add(s1);

                    //first part
                    result_start.add(s1);
                    result_end.add(s2);
                    String ref = "|".concat(scores1.get(i1).toString()).concat("|");
                    result_score.add(score_map.get(ref));
                    result_names.add(ref);

                    //overlapping part
                    result_start.add(s2);
                    result_end.add(e1);
                    String refm = "|".concat(scores1.get(i1).toString()).concat("|").concat(scores2.get(i2).toString());
                    result_score.add(score_map.get(refm));
                    result_names.add(refm);

                    //second part
                    result_start.add(e1);
                    result_end.add(e2);
                    String ref2 = "||".concat(scores2.get(i2).toString());
                    result_score.add(score_map.get(ref2));
                    result_names.add(ref2);


                    //outside part
                    result_start.add(e2);

                    i1++;
                    i2++;

                } else { //second interval is inside the first

                    //outside part
                    result_end.add(s1);

                    if (s1 != s2) {
                        result_start.add(s1);
                        result_end.add(s2);
                        String ref = "|".concat(scores1.get(i1).toString()).concat("|");
                        result_score.add(score_map.get(ref));
                        result_names.add(ref);
                    }

                    //overlapping part
                    result_start.add(s2);
                    result_end.add(e2);
                    String refm = "|".concat(scores1.get(i1).toString()).concat("|").concat(scores2.get(i2).toString());
                    result_score.add(score_map.get(refm));
                    result_names.add(refm);

                    if (e1 != e2) {
                        result_start.add(e2);
                        result_end.add(e1);
                        String ref2 = "||".concat(scores2.get(i2).toString());
                        result_score.add(score_map.get(ref2));
                        result_names.add(ref2);
                    }

                    //outside part

                    result_start.add(e1);

                    i1++;
                    i2++;
                }
            }
        }


        if (result_end.get(result_end.size() - 1) != genomeSize) {
            result_score.add(score_map.get("||"));
            result_names.add("||");
            result_end.add(genomeSize);
        }

        if(result_start.get(0) != 0L){
            result_start.add(0, 0L);
        } else {
            result_end.remove(0);
            result_score.remove(0);
            result_names.remove(0);
        }

        //set null values to 0.0
        result_score.stream().filter(val -> val == null).forEach(val -> val = 0.0);

        String name = intv1.getName() + "_" + intv2.getName();
        String desc = intv1.getDescription() + "_" + intv2.getDescription();
        return TrackFactory.getInstance().createScoredTrack(result_start, result_end, result_names, result_score, name, desc);
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
