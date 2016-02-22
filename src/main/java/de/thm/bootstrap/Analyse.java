package de.thm.bootstrap;

import de.thm.backgroundModel.BackgroundModel;
import de.thm.backgroundModel.RandomBackgroundModel;
import de.thm.calc.Intersect;
import de.thm.calc.IntersectCalculate;
import de.thm.calc.IntersectMultithread;
import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalLoader;
import de.thm.genomeData.Intervals;
import de.thm.mTrackBg.ScoreMultiTrackBackgroundModel;
import de.thm.positionData.Sites;
import de.thm.stat.ResultCollector;
import de.thm.stat.TestResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        IntervalLoader loader = IntervalLoader.getInstance();
        intervals =  loader.getAllIntervals();
        simple = new IntersectCalculate();
    }

    /**
     * Analysis of the user sites with all intervals and one background model
     *
     * @param userSites - sites for measurement
     */
    public void analyse(Sites userSites) throws Exception, IntervalTypeNotAllowedExcpetion {


        //IntersectResult resultUserSites;
        //IntersectResult resultBg;

        //Interval genes = intervals.get("knownGenes");
        //resultUserSites = simple.searchSingleInterval(genes, userSites);
        //Sites bg = new BetterBackgroundModel(resultUserSites.getIn(),resultUserSites.getOut() , genes);

        List<Interval> covariants = new ArrayList<>();
        //covariants.add(intervals.get("H1-hESC-H3K4m1"));
        //covariants.add(intervals.get("H1-hESC-H3K4m3"));
        //covariants.add(intervals.get("open-chrom-synth-HeLa-S3-valid"));
        //covariants.add(intervals.get("HeLa-S3-H3K4m1"));
        //covariants.add(intervals.get("exons"));
        //covariants.add(intervals.get("introns"));
        //covariants.add(intervals.get("cpg"));
        covariants.add(intervals.get("expression_blood.bed"));
        covariants.add(intervals.get("expression_fetal_brain.bed"));

        Interval blood_fetal  = Intervals.intersect(covariants);
        blood_fetal.setType(Interval.Type.inout);
        blood_fetal.setName("Expression Blood + Fetal brain");

        IntervalLoader.getInstance().addInterval("blood_fetal", blood_fetal);

        BackgroundModel bg = new ScoreMultiTrackBackgroundModel(userSites, covariants);
        //bg = new RandomBackgroundModel(userSites.getPositionCount());


        IntersectMultithread multi = new IntersectMultithread();

        ResultCollector collector = multi.execute(intervals, userSites, bg);

        System.out.println(collector.toString());

        Path path = Paths.get("/home/menzel/Desktop/THM/lfba/projekphase/MultiGenBrowser/src/web/");

        for(TestResult testResult: collector.getResultsByType(Interval.Type.score)){
            try {
                try (BufferedWriter writer = Files.newBufferedWriter(path.resolve(testResult.getFilename().concat(".json")))) {
                    writer.write("[");
                    writer.write(Arrays.toString(testResult.getResultMeasured().getResultScores().toArray()));
                    writer.write(",");
                    writer.write(Arrays.toString(testResult.getResultExpected().getResultScores().toArray()));
                    writer.write("]");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String toJsonArray(Map<String, Integer> names) {
        String r = "";

        r += "[";
        for(String name: names.keySet()){
            r += '"' + name + '"' + ',';
        }
        r = r.substring(0,r.length()-1);
        r += "],";
        r += "[";

        for(String name: names.keySet()){
            r += names.get(name) + ",";
        }
        r = r.substring(0,r.length()-1);
        r += "]";

        return r;
    }


    /**
     * Benchmarks the search with different position counts
     */
    public void benchmark(){
        String basePath = "/home/menzel/Desktop/THM/lfba/projekphase/dat/";

        Interval invExons = new Interval(new File(basePath + "inout/exons.bed"), Interval.Type.inout, "exons.bed");

        for(int i = 0 ; i < 20 ; i++){
            int j = i * 5000;

            Sites bg = new RandomBackgroundModel(j);
            long startTime = System.nanoTime();

            simple.searchSingleInterval(invExons, bg);

            long duration = System.nanoTime() - startTime;
            System.out.print(j + "\t"  + duration/1000000 + "\n");

        }

    }

}
