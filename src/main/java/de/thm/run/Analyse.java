package de.thm.run;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.Intersect;
import de.thm.calc.IntersectCalculate;
import de.thm.exception.CovariantsException;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.AbstractSites;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;

/**
 * Bootstraps basic analysis with all intervals one of the two background models and user input.
 * <p>
 * Created by Michael Menzel on 11/12/15.
 */
@Deprecated
public class Analyse {

    private List<Track> intervals;

    public Analyse() {
        TrackFactory loader = TrackFactory.getInstance();
        intervals = loader.getAllIntervals();
        Intersect simple = new IntersectCalculate();
    }

    /**
     * Analysis of the user sites with all intervals and one background model
     *
     * @param userSites - sites for measurement
     */
    public void analyse(Sites userSites) throws Exception {


        //IntersectResult resultUserSites;
        //IntersectResult resultBg;

        //Interval genes = intervals.get("knownGenes");
        //resultUserSites = simple.searchSingleInterval(genes, userSites);
        //Sites bg = new BetterBackgroundModel(resultUserSites.getIn(),resultUserSites.getOut() , genes);

        List<Track> covariants = new ArrayList<>();

        //covariants.add(intervals.get("H1-hESC-H3K4m1"));
        //covariants.add(intervals.get("H1-hESC-H3K4m3"));
        //covariants.add(intervals.get("open-chrom-synth-HeLa-S3-valid"));
        //covariants.add(intervals.get("HeLa-S3-H3K4m1"));
        //covariants.add(intervals.get("exons.bed"));
        //covariants.add(intervals.get("knownGenes.bed"));
        //covariants.add(intervals.get("cpg"));
        //covariants.add(intervals.get("expression_blood.bed"));
        for (Track track : TrackFactory.getInstance().getAllIntervals()) {
            if (track.getName().contains("genes"))
                covariants.add(track);
            if(track.getName().contains("exons"))
                covariants.add(track);
        }
        System.out.println("covariant: " + covariants.get(0).getName());

        Sites bg = BackgroundModelFactory.createBackgroundModel(100000);

        //IntersectMultithread multi = new IntersectMultithread();
        //ResultCollector collector = multi.execute(intervals, userSites, bg);

        IntersectCalculate calc  = new IntersectCalculate();

        calc.getAverageDistance((InOutTrack) covariants.get(1), userSites);

        System.out.printf("+++++++++++++++++++++");

        calc.getAverageDistance((InOutTrack) covariants.get(1), bg);

        System.out.printf("+++++++++++++++++++++");

    }



    public void timing(){


        List<Track> covariants = new ArrayList<>();
         for (Track track : TrackFactory.getInstance().getAllIntervals()) {
            if(track.getName().contains("lood"))
                covariants.add(track);
             if(track.getName().contains("node"))
                 covariants.add(track);
        }
        System.out.println("covariant: " + covariants);


        Sites random  = BackgroundModelFactory.createBackgroundModel(10000);
        Sites bg;


        for(int i = 1 ; i <= 10 ; i++) {
            BackgroundModelFactory.createBackgroundModel(covariants.get(0), random);
        }

        for(int i = 1 ; i <= 100 ; i++){

            try {

                final long startTime = System.currentTimeMillis();
                bg = BackgroundModelFactory.createBackgroundModel(covariants, random,i * 5000);

                long endTime = System.currentTimeMillis();
                System.out.println(bg.getPositionCount() +  "\t" +  (endTime - startTime) );


            } catch (CovariantsException e) {
                e.printStackTrace();
            }
        }

        AbstractSites sites = new AbstractSites() {
            @Override
            public List<Long> getPositions() {
                Long v = ChromosomSizes.getInstance().getGenomeSize();
                List<Long> list = new ArrayList<>();
                list.add(v);
                return list;
            }

            @Override
            public int getPositionCount() {
                return 1;
            }
        };
    }

}
