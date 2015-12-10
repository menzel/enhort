package de.thm.run;

import de.thm.calc.Intersect;
import de.thm.calc.IntersectBinarySearch;
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

        Sites bg = new SimpleBackgroundModel(userDat.getPositionCount());

        Intersect sec = new IntersectBinarySearch();
        //IntersectDual secDual = new IntersectDual();


        //System.out.println("binary: " + sec.searchSingleIntervall(invGenes, userDat));
        //System.out.println("dual: " + secDual.searchSingleIntervall(invGenes, userDat));

        System.out.print(sec.searchSingleIntervall(invExons, userDat));
        System.out.println(sec.searchSingleIntervall(invExons, bg));

        System.out.print(sec.searchSingleIntervall(invGenes, userDat));
        System.out.println(sec.searchSingleIntervall(invGenes, bg));

    }
}
