package de.thm.bootstrap;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.Intersect;
import de.thm.calc.IntersectCalculate;
import de.thm.calc.IntersectMultithread;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.positionData.Sites;
import de.thm.stat.ResultCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Bootstraps basic analysis with all intervals one of the two background models and user input.
 * <p>
 * Created by Michael Menzel on 11/12/15.
 */
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
        for (Track track : intervals) {
            if (track.getName().contains("ChromHMM"))
                covariants.add(track);
        }
        System.out.println("covariant: " + covariants.get(0).getName());

        Sites bg = BackgroundModelFactory.createBackgroundModel(covariants, userSites);

        IntersectMultithread multi = new IntersectMultithread();

        ResultCollector collector = multi.execute(intervals, userSites, bg);

        System.out.println(collector.toString());
    }


}
