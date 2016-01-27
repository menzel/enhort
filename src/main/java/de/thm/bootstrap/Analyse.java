package de.thm.bootstrap;

import de.thm.backgroundModel.AdvancedBackgroundModel;
import de.thm.backgroundModel.SimpleBackgroundModel;
import de.thm.calc.Intersect;
import de.thm.calc.IntersectCalculate;
import de.thm.calc.IntersectMultithread;
import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalLoader;
import de.thm.positionData.Sites;
import de.thm.resultCollector.ResultCollector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    public void analyse(Sites userSites){


        //IntersectResult resultUserSites;
        //IntersectResult resultBg;

        //Interval genes = intervals.get("knownGenes.bed");
        //resultUserSites = simple.searchSingleInterval(genes, userSites);
        //Sites bg = new BetterBackgroundModel(resultUserSites.getIn(),resultUserSites.getOut() , genes);

        List<Interval> covariants = new ArrayList<>();
        //covariants.add(intervals.get("H1-hESC-H3K4m1.bed"));
        //covariants.add(intervals.get("H1-hESC-H3K4m3.bed"));
        //covariants.add(intervals.get("open-chrom-synth-HeLa-S3-valid.bed"));
        //covariants.add(intervals.get("HeLa-S3-H3K4m1.bed"));
        //covariants.add(intervals.get("knownGenes.bed"));
        //covariants.add(intervals.get("exons_5UTR.bed"));
        //covariants.add(intervals.get("exons_3UTR.bed"));
        //covariants.add(intervals.get("open-chrom-synth-HeLa-S3-valid.bed"));
        //covariants.add(intervals.get("exons.bed"));
        //covariants.add(intervals.get("introns.bed"));
        //covariants.add(intervals.get("cpg.bed"));


        Sites bg;

        if(covariants.isEmpty()){
            bg = new SimpleBackgroundModel(userSites.getPositionCount());
        }
        else{
            bg = new AdvancedBackgroundModel(covariants,userSites);
        }

        // H_0: bg and user sites are independent. Large pValue: bg and user are independent. Small pValue: bg and user are dependent.
        // Large pValue (> 0.05): the insertion points look random
        // Small pValue (< 0.05): the insertion points are not random  (more interesting)


        IntersectMultithread multi = new IntersectMultithread(intervals, userSites, bg);


        System.out.println(ResultCollector.getInstance().toString());

        Path path = Paths.get("/home/menzel/Desktop/THM/lfba/projekphase/MultiGenBrowser/src/web/raw_data.csv");

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {

                writer.write("Question,1,2,3,4,5,N\n");
                writer.write(ResultCollector.getInstance().toCsv());
            }
        } catch (IOException e) {
            e.printStackTrace();
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

            simple.searchSingleInterval(invExons, bg);

            long duration = System.nanoTime() - startTime;
            System.out.print(j + "\t"  + duration/1000000 + "\n");

        }

    }

}
