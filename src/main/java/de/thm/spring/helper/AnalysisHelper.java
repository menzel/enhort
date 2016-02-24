package de.thm.spring.helper;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.IntersectMultithread;
import de.thm.exception.CovariantsException;
import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalFactory;
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
        Sites bg = BackgroundModelFactory.createBackgroundModel(input.getPositionCount());

        IntersectMultithread multi;
        multi = new IntersectMultithread();
        return multi.execute(Server.getIntervals(), input, bg);
    }

    public static ResultCollector runAnalysis(Sites sites, List<String> covariantNames) throws CovariantsException {

        List<Interval> covariants = getCovariants(covariantNames);

        Sites bg = BackgroundModelFactory.createBackgroundModel(covariants, sites);

        IntersectMultithread multi = new IntersectMultithread();
        return multi.execute(Server.getIntervals(), sites, bg);

    }

    private static List<Interval> getCovariants(List<String> covariantNames) {
        List<Interval> intervals = new ArrayList<>();
        IntervalFactory loader = IntervalFactory.getInstance();

        Map<String, Interval> knownIntervals = loader.getAllIntervals();
        for(String name: covariantNames){
            if(knownIntervals.containsKey(name)){
                intervals.add(knownIntervals.get(name));
            }
        }

        return intervals;
    }

}
