package de.thm.backgroundModel;

import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Background model for multiple tracks which have scored values.
 * <p>
 * Created by Michael Menzel on 17/2/16.
 */
class SecondScoreMultiTrackBackgroundModel implements Sites {

    private final GenomeFactory.Assembly assembly;
    private List<Long> positions = new ArrayList<>();

    SecondScoreMultiTrackBackgroundModel(GenomeFactory.Assembly assembly) {
        this.assembly = assembly;
    }


     /**
     * Constructor
     *
     * @param sites      - sites to build model against.
     * @param covariant - single covariant
     */
    SecondScoreMultiTrackBackgroundModel(GenomeFactory.Assembly assembly, ScoredTrack covariant, Sites sites, int minSites, double influence) {

        this(assembly, Collections.singletonList(covariant),sites, minSites, influence);
    }


    /**
     * Constructor
     *
     *
     * @param sites      - sites to build model against.
     * @param covariants - list of intervals to build model against.
     */
    SecondScoreMultiTrackBackgroundModel(GenomeFactory.Assembly assembly, List<ScoredTrack> covariants, Sites sites, int minSites, double influence) {
        this.assembly = assembly;
        ScoredTrack interval = generateProbabilityInterval(sites, covariants, influence);

        int count = (sites.getPositionCount() > minSites) ? sites.getPositionCount() : minSites;
        Collection<Long> pos = generatePositionsByProbability(interval, count);

        positions.addAll(pos);
    }


    /**
     * Generates an interval with probabilities as scores based on the intervals given and the sites.
     * In the names of the interval is the original score combination based on the hashing.
     *
     * @param sites     - sites to set probability by.
     * @param intervals - list of intervals as covariants.
     * @return new interval with probability scores.
     */
    ScoredTrack generateProbabilityInterval(Sites sites, List<ScoredTrack> intervals, double influence) {


        Map<ScoreSet, Double> sitesOccurence = fillOccurenceMap(intervals, sites);

        smooth(sitesOccurence, 1);

        double sum = sites.getPositionCount();
        for (ScoreSet k : sitesOccurence.keySet())
            sitesOccurence.put(k, sitesOccurence.get(k) / sum);

        ScoredTrack interval = combine(intervals, sitesOccurence);


        // Fill occurences maps over whole genome

        List<Long> starts = interval.getStarts();
        List<Long> ends = interval.getEnds();

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
        long genome = ChromosomSizes.getInstance().getGenomeSize(assembly);

        for (int i = 0; i < intervalScore.size(); i++) {
            Double p = intervalScore.get(i);

            if (p == null) {
                newScores.add(0d);

            } else {

                double genomeLength = lengths.get(keys.get(i));
                double length = ends.get(i) - starts.get(i);

                if(genomeLength != 0) { //this can happen with multiple scored tracks

                    double prob = p * (length/genomeLength);
                    prob = influence * prob + (1 - influence) * (length/genome);

                    //add probability to score list
                    newScores.add(prob);
                } else{
                    // check if start stop positions are present
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
                interval.getStarts(),
                interval.getEnds(),
                interval.getIntervalName(),
                newScores,
                interval.getName(),
                interval.getDescription());
    }


    /**
     * Computes an occurence map which holds information about how often a score combination from the given intervals is picked by one of the given sites.
     * The returning map contains the counts per score.
     *
     * @param tracks - scores to get from.
     * @param sites     - positions to look up.
     * @return map<Score, Count> to score combination to  probablity
     */
    Map<ScoreSet, Double> fillOccurenceMap(List<ScoredTrack> tracks, Sites sites) {
        Map<ScoreSet, Double> map = new HashMap<>(); //holds the conversion between score and probability
        Map<Track, Integer> indices = new HashMap<>(); //indices of the tracks during calc

        //init indices map:
        for (Track track : tracks) {
            indices.put(track, 0);
        }

        for (Long p : sites.getPositions()) {
            ScoreSet key = new ScoreSet(tracks.size());

            for (ScoredTrack track : tracks) {

                List<Long> intervalStart = track.getStarts();
                List<Long> intervalEnd = track.getEnds();

                int i = indices.get(track);
                int intervalCount = intervalStart.size() - 1;


                    while (i < intervalCount && intervalEnd.get(i) <= p)
                        i++;

                    if (p >= intervalStart.get(i)) {

                        key.add(track.getIntervalScore().get(i), tracks.indexOf(track));

                    } else {
                        key.add(null, tracks.indexOf(track));
                    }


                indices.put(track, i);

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
        List<Long> starts = probabilityInterval.getStarts();
        List<Long> ends = probabilityInterval.getEnds();
        List<Double> probabilities = probabilityInterval.getIntervalScore();
        List<Double> random = new ArrayList<>();
        MersenneTwister rand;

        rand  = new MersenneTwister();

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

                // TODO FIX: IndexOutOfBoundsException  Index: 0, Size: 0
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
     * @param tracks - list of tracks
     * @param score_map - map of score names to probabilities. The score names should match the scores in intv1 and intv2
     *
     * @return Interval of type GenomeInterval
     */
    ScoredTrack combine(List<ScoredTrack> tracks, Map<ScoreSet, Double> score_map) {

        List<Long> new_start = new ArrayList<>();
        List<Long> new_end = new ArrayList<>();
        List<Double> new_score = new ArrayList<>();
        List<String> new_names = new ArrayList<>();


        // take all start and ends, combine in lists and sort
        for(ScoredTrack track: tracks){
            new_start.addAll(track.getStarts());
            new_start.addAll(track.getEnds());
            Collections.sort(new_start);

            new_end.addAll(track.getStarts());
            new_end.addAll(track.getEnds());
            Collections.sort(new_end);
        }



        //check if 0 and genome size is present
        if(new_start.get(0) != 0L){
            new_start.add(0,0L); // and add 0 as first start
        } else {
            new_start.add(1,1L);
            //TODO check
        }

        if(new_end.get(new_end.size()-1) != ChromosomSizes.getInstance().getGenomeSize(assembly)){
            //new_start.add(new_start.size(), new_end.get(new_end.size()-1)); // add last end as start
            new_end.add(new_end.size(), ChromosomSizes.getInstance().getGenomeSize(assembly)); // and genome size as end
        }

        //delete intervals with length 0
        for(int i = 0;i < new_start.size()-1; i++){

            if(new_start.get(i).equals(new_end.get(i))){
                new_start.remove(i);
                new_end.remove(i);
            }
        }


        if(new_start.size() != new_end.size()){
            try {
                throw new Exception("Lists do not have equal length");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // add enough scoreSets:
        List<ScoreSet> scoredSet = Collections.synchronizedList(new ArrayList<>());

        for(int i = 0 ; i < new_start.size(); i++) {
            scoredSet.add(new ScoreSet(tracks.size()));
        }

        // fill scores sets in a thread for each track
        for(ScoredTrack track: tracks){
            Runnable runner = new createScoreSet(tracks.indexOf(track), track, scoredSet, new_start, new_end);
            Thread one = new Thread(runner);
            one.run();
        }

        // set scoreSets to scores and add hash codes to names list
        for(ScoreSet set: scoredSet){

            if(score_map.containsKey(set)){
                new_score.add(score_map.get(set));
                new_names.add(String.valueOf(set.hashCode()));
            }
            else {
                new_score.add(0.);
                new_names.add("0");
            }
        }


        return TrackFactory.getInstance().createScoredTrack(new_start, new_end, new_names, new_score, "combined", "combined");

    }


    /**
     * Smoothes occurence counts over the scores map
     *
     * @param sitesOccurence map to smooth
     * @param factor  - factor by which the smoothing is applied
     */
    private void smooth(Map<ScoreSet, Double> sitesOccurence, double factor) {

        if(factor == 0.)
            return;

        //TODO. do multidimensional smoothing or decrease dimensions
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

    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }

    /**
     *  Inner runner class to generate a list of scores (scoreSet) for each interval in the combined track
     */
    private class createScoreSet implements Runnable {

        private final ScoredTrack track;
        private final List<ScoreSet> scoredSet;
        private final List<Long> new_start;
        private final List<Long> new_end;
        private int position;

        createScoreSet(int position, ScoredTrack track, List<ScoreSet> scoredSet, List<Long> new_start, List<Long> new_end) {
            this.position = position;

            this.track = track;
            this.scoredSet = scoredSet;
            this.new_start = new_start;
            this.new_end = new_end;
        }

        @Override
        public void run() {

            int j = 0;

            // get scores from the map
            for(int i = 0 ; i < new_start.size(); i ++) { // for each interval from the new track
                Long start = new_start.get(i);
                Long end = new_end.get(i);

                //get ScoreSet from all tracks
                ScoreSet current = scoredSet.get(i);

                if (track.getStarts().contains(start)) {
                    //if the start is exacly in the track get score
                    current.add(track.getIntervalScore().get(track.getStarts().indexOf(start)), position);
                    continue;
                }

                while (j < track.getStarts().size() - 1 && track.getEnds().get(j) <= end)
                    j++;

                if (start >= track.getStarts().get(j)) {
                    current.add(track.getIntervalScore().get(j), position); //intervals overlap

                } else {
                    current.add(null, position);  //outside for this track
                }
            }
        }
    }

}
