package de.thm.run;

import de.thm.calc.Intersect;
import de.thm.calc.IntersectBinarySearch;
import de.thm.calc.IntersectDual;
import de.thm.genomeData.IntervalDual;
import de.thm.positionData.SimpleBackgroundModel;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;

import java.io.File;

public class Main {

    public static void main(String[] args) {


        Sites userDat = new UserData();
        ((UserData) userDat).loadPositionsFromFile(new File("/home/menzel/Desktop/THM/lfba/projekphase/genomic_sites.sleeping_beauty.hg19.txt"));

        //Interval inv = new IntervalData(new File("/home/menzel/Desktop/THM/lfba/projekphase/knownGene.txt"));
        IntervalDual invGenes = new IntervalDual(new File("/home/menzel/Desktop/THM/lfba/projekphase/knownGene.txt"));
        IntervalDual invExons = new IntervalDual(new File("/home/menzel/Desktop/THM/lfba/projekphase/exons.txt"));


        Intersect sec = new IntersectBinarySearch();
        IntersectDual secDual = new IntersectDual();

        //Sites bg = new SimpleBackgroundModel(2000);

        //System.out.println("binary: " + sec.searchSingleIntervall(invGenes, userDat));
        //System.out.println("dual: " + secDual.searchSingleIntervall(invGenes, userDat));

        for(int i = 0; i < 10000; i++){
            int j = i*10;

            Sites bg = new SimpleBackgroundModel(j);

            long startTime = System.currentTimeMillis();

            System.out.print(secDual.searchSingleIntervall(invExons, bg));

            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            System.out.print(totalTime + "\t");

            startTime = System.currentTimeMillis();

            sec.searchSingleIntervall(invExons, bg);

            endTime   = System.currentTimeMillis();
            totalTime = endTime - startTime;

            System.out.println(totalTime);
        }
    }
}
