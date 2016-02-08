package de.thm.spring.helper;

import de.thm.backgroundModel.BackgroundModel;
import de.thm.backgroundModel.MultiTrackBackgroundModel;
import de.thm.backgroundModel.RandomBackgroundModel;
import de.thm.backgroundModel.SingleTrackBackgroundModel;
import de.thm.calc.IntersectMultithread;
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

    public static ResultCollector runAnalysis(Sites input){
        BackgroundModel bg = new RandomBackgroundModel(input.getPositionCount());

        IntersectMultithread multi;
        multi = new IntersectMultithread();
        return multi.execute(Server.getIntervals(), input, bg);
    }

    public static ResultCollector runAnalysis(Sites sites, List<String> covariantNames){

        List<Interval> covariants = getCovariants(covariantNames);

        BackgroundModel bg;

        try {
            if(covariants.size() == 1 && covariants.get(0).getType() == Interval.Type.score){
                bg = new SingleTrackBackgroundModel(covariants.get(0), sites);
            } else if(covariants.isEmpty()){
                bg = new RandomBackgroundModel(sites.getPositionCount());
            }else{
                bg = new MultiTrackBackgroundModel(covariants,sites);
            }

            IntersectMultithread multi;
            multi = new IntersectMultithread();

            return multi.execute(Server.getIntervals(), sites, bg);

        } catch (Exception e) {
            e.printStackTrace(); //TODO
            return null;
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