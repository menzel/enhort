package de.thm.run;

import de.thm.calc.Intersect;
import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalData;
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

        Intersect sec = new Intersect();

        System.out.println("Running:");

        System.out.println(sec.searchSingleIntervall(inv, userDat));
        System.out.println(sec.searchSingleIntervall(inv, bg));



    }
}
