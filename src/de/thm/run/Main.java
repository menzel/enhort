package de.thm.run;

import de.thm.calc.Intersect;
import de.thm.calc.IntersectBinarySearch;
import de.thm.calc.IntersectBinarySearchNamed;
import de.thm.genomeData.IntervalNamed;
import de.thm.positionData.SimpleBackgroundModel;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        //load data

        Sites userDat = new UserData();
        ((UserData) userDat).loadPositionsFromFile(new File("/home/menzel/Desktop/THM/lfba/projekphase/genomic_sites.sleeping_beauty.hg19.txt"));

        IntervalNamed invGenes = new IntervalNamed(new File("/home/menzel/Desktop/THM/lfba/projekphase/knownGenes.bed"));

        /*
        Interval invExons = new IntervalDual(new File("/home/menzel/Desktop/THM/lfba/projekphase/exons.txt"));
        */

        //IntervalNamed invHmm = new IntervalNamed(new File("/home/menzel/Desktop/THM/lfba/projekphase/hmm.txt"));

        // get calculators

        Sites bg = new SimpleBackgroundModel(userDat.getPositionCount());
        Intersect sec = new IntersectBinarySearch();
        IntersectBinarySearchNamed secNamed = new IntersectBinarySearchNamed();

        System.out.println(sec.searchSingleIntervall(invGenes,userDat));
        System.out.println(sec.searchSingleIntervall(invGenes,bg));

        /*
        // calculate

        // Genes:

        System.out.print(sec.searchSingleIntervall(invGenes, userDat));
        System.out.println(sec.searchSingleIntervall(invGenes, bg));

        System.out.println("P-value: " + QuiSquareTest.chiSquareTest(sec.searchSingleIntervall(invGenes, userDat), sec.searchSingleIntervall(invGenes, bg)));

        // Exons:

        System.out.print(sec.searchSingleIntervall(invExons, userDat));
        System.out.println(sec.searchSingleIntervall(invExons, bg));

        System.out.println("P-value: " +  QuiSquareTest.chiSquareTest(sec.searchSingleIntervall(invGenes, userDat), sec.searchSingleIntervall(invGenes, bg)));

        System.out.println(secNamed.searchSingleIntervall(invHmm,userDat));
        System.out.println(secNamed.searchSingleIntervall(invHmm,bg));
        */
    }
}
