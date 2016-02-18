package de.thm.mTrackBg;

import de.thm.backgroundModel.BackgroundModel;
import de.thm.backgroundModel.SingleTrackBackgroundModel;
import de.thm.genomeData.Interval;
import de.thm.genomeData.Intervals;
import de.thm.positionData.Sites;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Michael Menzel on 17/2/16.
 */
public class ScoreMultiTrackBackgroundModel extends BackgroundModel{

    public ScoreMultiTrackBackgroundModel(Sites sites, List<Interval> intervals){
        AppearanceTable appearanceTable = new AppearanceTable();
        appearanceTable.fillTable(intervals,sites);

        positions.addAll(generatePositions(appearanceTable, intervals));
        this.hash = positions.hashCode();
    }

    private Collection<Long> generatePositions(AppearanceTable appearanceTable, List<Interval> intervals) {

        Map<String, Interval> probIntervals = new HashMap<>();

        for(String app: appearanceTable.getKeySet()){
            if(app.compareTo("[]") == 0){ //TODO handle outside params
                continue;
            }

            Map<Double, Integer> scoreToCount = appearanceTable.get(app);

            List<Interval> currentIntervals = appearanceTable.translate(app, intervals);
            List<Interval> negativeIntervals = appearanceTable.translateNegative(intervals, app);

            for(double score: scoreToCount.keySet()){

                //create intervals which only contain the current score
                currentIntervals = currentIntervals.stream().map(i -> Intervals.subsetScore(i, score)).collect(Collectors.toList());
                negativeIntervals = negativeIntervals.stream().map(i -> Intervals.subsetScore(i, score)).collect(Collectors.toList());

                //combine intervals, substract others
                currentIntervals.addAll(negativeIntervals.stream().map(Interval::invert).collect(Collectors.toList()));

                Interval interval = Intervals.intersect(currentIntervals);

                double prob = appearanceTable.getProb(app, score);


                // probability for score in all available scores
                long count = 0;
                for(Interval i: currentIntervals)
                    count += i.getIntervalScore().stream().filter(j -> j.equals(score)).count();

                prob /= count;

                interval.setIntervalScore(prob);

                probIntervals.put(app + score, interval);
            }
        }

        //double sum = probabilityScores.size();
        //probabilityScores = probabilityScores.stream().map(p -> p/sum).collect(Collectors.toList());

        SingleTrackBackgroundModel better = new SingleTrackBackgroundModel();

        Collection<Interval> tmp = new TreeMap<>(probIntervals).values();

        Interval probInterval = Intervals.combine(tmp.stream().collect(Collectors.toList()));

        return better.generatePositonsByProbability(probInterval, 1000);

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

}
