package de.thm.run;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.Intersect;
import de.thm.calc.IntersectMultithread;
import de.thm.calc.TestTrack;
import de.thm.exception.CovariantsException;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.AbstractSites;
import de.thm.positionData.Sites;
import de.thm.stat.ResultCollector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        TestTrack simple = new Intersect();
    }



    /**
     * Analysis of the user sites with all intervals and one background model
     *
     * @param userSites - sites for measurement
     */
    public void analyse(Sites userSites) throws Exception {


        //TestTrackResult resultUserSites;
        //TestTrackResult resultBg;

        //Interval genes = intervals.get("knownGenes");
        //resultUserSites = simple.searchTrack(genes, userSites);
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
            if(track.getName().contains("blood"))
                covariants.add(track);
        }
        System.out.println("covariant: " + covariants.get(0).getName());

        Sites bg = BackgroundModelFactory.createBackgroundModel(covariants, userSites,1);

        IntersectMultithread multi = new IntersectMultithread();
        ResultCollector collector = multi.execute(intervals, userSites, bg);

        System.out.println(collector);


    }



    public void timing(){

        TrackFactory.getInstance().loadIntervals();


        List<Track> covariants = new ArrayList<>();
         for (Track track : TrackFactory.getInstance().getAllIntervals()) {
            if(track.getName().contains("lood")) covariants.add(track);
            if(track.getName().contains("node")) covariants.add(track);
        }
        System.out.println("covariant: " + covariants);


        Sites random  = BackgroundModelFactory.createBackgroundModel(10000);
        Sites bg = null;


        for(int i = 1 ; i <= 10 ; i++) {
            BackgroundModelFactory.createBackgroundModel(covariants.get(0), random, 10000, 1);
        }

        for(int i = 1 ; i <= 100 ; i++){
            List<Long> sum = new ArrayList<>();


                for(int j = 0; j < 5; j++) {

                    final long startTime = System.currentTimeMillis();
                    try {
                        bg = BackgroundModelFactory.createBackgroundModel(covariants, random, i * 5000, 1.0); // bg with covariants
                    } catch (CovariantsException e) {
                        e.printStackTrace();
                    }

                    long endTime = System.currentTimeMillis();
                    sum.add(endTime - startTime);
                }

                System.out.println(bg.getPositionCount() +  "\t" + median(sum) + "\t" + Arrays.toString(sum.toArray()));


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

        public void timing_intersect(){

            Intersect intersect = new Intersect<InOutTrack>();

            List<Track> tracks = new ArrayList<>();


            RandomTrack foo1 = new RandomTrack(5000);

            Sites foo = BackgroundModelFactory.createBackgroundModel(50000);
            for(int i = 1 ; i <= 10 ; i++){
                searchSingleInterval(foo1, foo);
            }

            System.out.println(foo1.getIntervalsStart().size());


            for(int k  = 1 ; k <= 20; k++) {

                for (int i = 1; i <= 20; i++) {
                    List<Long> sum = new ArrayList<>();
                    RandomTrack rand = new RandomTrack(k * 100000);

                    for (int j = 0; j < 10; j++) {

                        Sites bg = BackgroundModelFactory.createBackgroundModel(i * 10000);

                        final long startTime = System.currentTimeMillis();

                        searchSingleInterval(rand, bg);

                        long endTime = System.currentTimeMillis();
                        sum.add(endTime - startTime);
                    }

                    System.out.println(k * 100000 + "\t"  + i * 10000 + "\t" + median(sum));
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


    public void searchSingleInterval(RandomTrack intv, Sites pos) {
        int out = 0;
        int in = 0;
        int i = 0;


        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();

        int intervalCount = intervalStart.size() - 1;


        for (Long p : pos.getPositions()) {

            while (i < intervalCount && intervalEnd.get(i) <= p)
                i++;

            if (p >= intervalStart.get(i)) in++;
            else out++;
        }
    }






    private long median(List<Long> sum) {
        Collections.sort(sum);
        int mid = sum.size()/2;

        if(sum.size()%2 ==1 ){
            return sum.get(mid);
        } else {
            return sum.get(mid-1) + sum.get(mid);
        }

    }

}
