package de.thm.bootstrap;

import de.thm.calc.Intersect;
import de.thm.calc.IntersectSimple;
import de.thm.calc.Result;
import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalLoader;
import de.thm.positionData.BetterBackgroundModel;
import de.thm.positionData.SimpleBackgroundModel;
import de.thm.positionData.Sites;
import de.thm.stat.IndependenceTest;

import java.io.File;
import java.util.Map;

/**
 * Bootstraps basic analysis with all intervals one of the two background models and user input.
 *
 * Created by Michael Menzel on 11/12/15.
 */
public class Analyse {

    private Intersect simple;
    private Map<String, Interval> intervals;

    public Analyse() {
        IntervalLoader loader = new IntervalLoader();
        intervals =  loader.getAllIntervals();


        simple = new IntersectSimple();
    }

    /**
     * Analysis of the user sites with all intervals and one background model
     *
     * @param userSites - sites for measurement
     */
    public void analyse(Sites userSites){

        //Sites bg = new SimpleBackgroundModel(userSites.getPositionCount());

        Result resultUserSites;
        Result resultBg;

        Interval genes = intervals.get("knownGenes.bed");
        resultUserSites = simple.searchSingleInterval(genes, userSites);
        Sites bg = new BetterBackgroundModel(resultUserSites.getIn(),resultUserSites.getOut() , genes);


        // H_0: bg and user sites are independent. Large pValue: bg and user are independent. Small pValue: bg and user are dependent.
        // Large pValue (> 0.05): the insertion points look random
        // Small pValue (< 0.05): the insertion points are not random  (more interesting)

        for(String intervalName: intervals.keySet()){
            resultUserSites = simple.searchSingleInterval(intervals.get(intervalName), userSites);
            resultBg = simple.searchSingleInterval(intervals.get(intervalName), bg);

            IndependenceTest tester = new IndependenceTest();
            double pValue = tester.test(resultUserSites, resultBg);
            System.out.println(intervalName + " p-value: " + pValue);
        }

    }


    /**
     * Benchmarks the search with different position counts
     */
    public void benchmark(){
        String basePath = "/home/menzel/Desktop/THM/lfba/projekphase/dat/";

        Interval invExons = new Interval(new File(basePath + "inout/exons.bed"), Interval.Type.inout);

        for(int i = 0 ; i < 20 ; i++){
            int j = i * 5000;

            Sites bg = new SimpleBackgroundModel(j);
            long startTime = System.nanoTime();

            simple.searchSingleInterval(invExons,bg);

            long duration = System.nanoTime() - startTime;
            System.out.print(j + "\t"  + duration/1000000 + "\n");

        }

    }

}
