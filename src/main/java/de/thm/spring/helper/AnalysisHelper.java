package de.thm.spring.helper;

import de.thm.backgroundModel.BackgroundModel;
import de.thm.backgroundModel.MultiTrackBackgroundModel;
import de.thm.backgroundModel.RandomBackgroundModel;
import de.thm.backgroundModel.ScoreMultiTrackBackgroundModel;
import de.thm.calc.IntersectMultithread;
import de.thm.exception.CovariantsException;
import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalLoader;
import de.thm.positionData.Sites;
import de.thm.run.Server;
import de.thm.stat.ResultCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Michael Menzel on 4/2/16.
 */
public class AnalysisHelper {

    private static final int maxCovariants = 7;

    public static ResultCollector runAnalysis(Sites input){
        BackgroundModel bg = new RandomBackgroundModel(input.getPositionCount());

        IntersectMultithread multi;
        multi = new IntersectMultithread();
        return multi.execute(Server.getIntervals(), input, bg);
    }

    public static ResultCollector runAnalysis(Sites sites, List<String> covariantNames) throws CovariantsException {

        List<Interval> covariants = getCovariants(covariantNames);

        BackgroundModel bg;

            if(covariants.size() < maxCovariants) {

                try {
                    if (covariants.isEmpty()) {
                        bg = new RandomBackgroundModel(sites.getPositionCount());
                    } else if (covariants.stream().allMatch(i -> i.getType() == Interval.Type.score)) {
                        bg = new ScoreMultiTrackBackgroundModel(covariants, sites);
                    } else {
                        bg = new MultiTrackBackgroundModel(covariants, sites);
                    }


                    IntersectMultithread multi;
                    multi = new IntersectMultithread();

                    return multi.execute(Server.getIntervals(), sites, bg);

                } catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
                    intervalTypeNotAllowedExcpetion.printStackTrace();
                    return null;
                }

            }  else{
                throw new CovariantsException("Too many covariants selected by user");
            }
    }

    private static List<Interval> getCovariants(List<String> covariantNames) {
        List<Interval> intervals = new ArrayList<>();
        IntervalLoader loader = IntervalLoader.getInstance();

        Map<String, Interval> knownIntervals = loader.getAllIntervals();
        for(String name: covariantNames){
            if(knownIntervals.containsKey(name)){
                intervals.add(knownIntervals.get(name));
            }
        }

        return intervals;
    }

}
