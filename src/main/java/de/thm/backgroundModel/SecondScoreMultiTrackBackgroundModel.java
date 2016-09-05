package de.thm.backgroundModel;

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
class SecondScoreMultiTrackBackgroundModel implements Sites {

    private List<Long> positions = new ArrayList<>();

    SecondScoreMultiTrackBackgroundModel() {}


     /**
     * Constructor
     *
     * @param sites      - sites to build model against.
     * @param covariant - single covariant
     */
    SecondScoreMultiTrackBackgroundModel(ScoredTrack covariant, Sites sites, int minSites, double influence) {

        this(Collections.singletonList(covariant),sites, minSites, influence);
    }


    /**
     * Constructor
     *
     *
     * @param sites      - sites to build model against.
     * @param covariants - list of intervals to build model against.
     */
    SecondScoreMultiTrackBackgroundModel(List<ScoredTrack> covariants, Sites sites, int minSites, double influence) {
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

        double sum = sites.getPositionCount();
        for (ScoreSet k : sitesOccurence.keySet())
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
        long genome = ChromosomSizes.getInstance().getGenomeSize();

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
                interval.getIntervalsStart(),
                interval.getIntervalsEnd(),
                interval.getIntervalName(),
                newScores,
                interval.getName(),
                interval.getDescription());
    }

    /**
     * Computes an occurence map which holds information about how often a score combination from the given intervals is picked by one of the given sites.
     * The returning map contains the counts per score.
     *
     * @param intervals - scores to get from.
     * @param sites     - positions to look up.
     * @return map<Score, Count> to score combination to  probablity
     */
    Map<ScoreSet, Double> fillOccurenceMap(List<ScoredTrack> intervals, Sites sites) {
        Map<ScoreSet, Double> map = new HashMap<>(); //holds the conversion between score and probability
        Map<Track, Integer> indices = new HashMap<>(); //indices of the tracks during calc

        //init indices map:
        for (Track track : intervals) {
            indices.put(track, 0);
        }

        for (Long p : sites.getPositions()) {
            ScoreSet key = new ScoreSet(intervals.size());

            for (ScoredTrack interval : intervals) {

                List<Long> intervalStart = interval.getIntervalsStart();
                List<Long> intervalEnd = interval.getIntervalsEnd();

                int i = indices.get(interval);
                int intervalCount = intervalStart.size() - 1;


                    while (i < intervalCount && intervalEnd.get(i) <= p)
                        i++;

                    if (p >= intervalStart.get(i)) {

                        key.add(interval.getIntervalScore().get(i));

                    } else {
                        key.add(null);
                    }


                indices.put(interval, i);

            }

            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1.);
            }
        }
        //TODO Apply smoothing over map here
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
     * @param tracks - list of tracks
     * @param score_map - map of score names to probabilities. The score names should match the scores in intv1 and intv2
     *
     * @return Interval of type GenomeInterval
     */
    private ScoredTrack combine(List<ScoredTrack> tracks, Map<ScoreSet, Double> score_map) {

        List<Long> new_start = new ArrayList<>();
        List<Long> new_end = new ArrayList<>();
        List<Double> new_score = new ArrayList<>();
        List<String> new_names = new ArrayList<>();

        Map<Track, Integer> indices = new HashMap<>(); //indices of the tracks during calc

        //init indices map:
        for (Track track: tracks) {
            indices.put(track, 0);
        }


        // take all start and ends, combine in lists and sort
        for(ScoredTrack track: tracks){
            new_start.addAll(track.getIntervalsStart());
            new_start.addAll(track.getIntervalsEnd());
            Collections.sort(new_start);

            new_end.addAll(track.getIntervalsStart());
            new_end.addAll(track.getIntervalsEnd());
            Collections.sort(new_end);
        }


        //check if 0 and genome size is present
        if(new_start.get(0) != 0L){
            new_end.add(new_start.get(0)); //add first start as first end
            new_start.add(0,0L); // and add 0 as first start
        } else {
            // TODO
        }

        if(new_end.get(new_end.size()-1) != ChromosomSizes.getInstance().getGenomeSize()){
            new_start.add(new_start.size(), new_end.get(new_end.size()-1)); // add last end as start
            new_end.add(new_end.size(), ChromosomSizes.getInstance().getGenomeSize()); // and genome size as end
        }

        if(new_start.size() != new_end.size()){
            try {
                throw new Exception("Lists do not have equal length");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // get scores from the map
        for(int i = 0 ; i < new_start.size(); i ++){ // for each interval from the new track
            Long start = new_start.get(i);
            Long end = new_end.get(i);

            //get ScoreSet from all tracks
            ScoreSet current = new ScoreSet(tracks.size());

            for(ScoredTrack track: tracks){

                if(track.getIntervalsStart().contains(start)){
                    //if the start is exacly in the track get score
                    current.add(track.getIntervalScore().get(track.getIntervalsStart().indexOf(start)));
                    continue;
                }

                //for(int j = indices.get(track); j < track.getIntervalsEnd().size(); j++){
                int j = indices.get(track);

                while(j < track.getIntervalsStart().size()-1 && track.getIntervalsEnd().get(j) <= end)
                    j++;

                if(start >= track.getIntervalsStart().get(j)){
                    current.add(track.getIntervalScore().get(j)); //intervals overlap

                } else {
                    current.add(null);  //outside for this track
                }
            }

            new_score.add(score_map.get(current));
            new_names.add(String.valueOf(current.hashCode()));
        }

        return TrackFactory.getInstance().createScoredTrack(new_start, new_end, new_names, new_score, "combined", "combined");

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
