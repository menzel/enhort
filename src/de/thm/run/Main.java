package de.thm.run;

import de.thm.calc.Intersect;
import de.thm.calc.IntersectDual;
import de.thm.calc.IntersectSimple;
import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalData;
import de.thm.genomeData.IntervalDual;
import de.thm.positionData.SimpleBackgroundModel;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        System.out.println("Reading Data:");

        Sites userDat = new UserData();
        ((UserData) userDat).loadPositionsFromFile(new File("/home/menzel/Desktop/THM/lfba/projekphase/genomic_sites.sleeping_beauty.hg19.txt"));

        Sites bg = new SimpleBackgroundModel(userDat.getPositionCount());

        Interval inv = new IntervalData(new File("/home/menzel/Desktop/THM/lfba/projekphase/knownGene.txt"));
        IntervalDual invDual = new IntervalDual(new File("/home/menzel/Desktop/THM/lfba/projekphase/knownGene.txt"));

        Intersect sec = new IntersectSimple();
        //Intersect sec = new IntersectBinarySearch();
        IntersectDual secDual = new IntersectDual();

        System.out.println("Running:");
        long startTime = System.currentTimeMillis();

        System.out.println(secDual.searchSingleIntervall(invDual, userDat));
        System.out.println(secDual.searchSingleIntervall(invDual, bg));


        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);

        // Another run:

        startTime = System.currentTimeMillis();

        System.out.println(sec.searchSingleIntervall(inv, userDat));
        System.out.println(sec.searchSingleIntervall(inv, bg));

        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println(totalTime);
    }
}
