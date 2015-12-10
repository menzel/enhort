package de.thm.run;

import de.thm.calc.Intersect;
import de.thm.calc.IntersectAnotherBinarySearch;
import de.thm.calc.IntersectDual;
import de.thm.genomeData.IntervalDual;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {


        Sites userDat = new UserData();
        ((UserData) userDat).loadPositionsFromFile(new File("/home/menzel/Desktop/THM/lfba/projekphase/genomic_sites.sleeping_beauty.hg19.txt"));

        //Interval inv = new IntervalData(new File("/home/menzel/Desktop/THM/lfba/projekphase/knownGene.txt"));
        IntervalDual invGenes = new IntervalDual(new File("/home/menzel/Desktop/THM/lfba/projekphase/knownGene.txt"));
        //IntervalDual invExons = new IntervalDual(new File("/home/menzel/Desktop/THM/lfba/projekphase/exons.txt"));


        Intersect sec = new IntersectAnotherBinarySearch();
        IntersectDual secDual = new IntersectDual();

        //Sites bg = new SimpleBackgroundModel(2000);

        System.out.println("binary: " + sec.searchSingleIntervall(invGenes, userDat));
        System.out.println("dual: " + secDual.searchSingleIntervall(invGenes, userDat));

        /*
        for(int i = 0; i < 100; i++){
            int j = i*5000;

            Sites bg = new SimpleBackgroundModel(j);

            long startTime = System.currentTimeMillis();

            System.out.print(secDual.searchSingleIntervall(invExons, bg));

            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println(totalTime + "\t");
        }
        */




        //System.out.println(sec.searchSingleIntervall(inv, userDat));
        //System.out.println(sec.searchSingleIntervall(inv, bg));

    }

    private static void writeExons(IntervalDual invExons, File file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())){

            for (String chromosom : invExons.getIntervalStarts().keySet()) {


                ArrayList<Long> intervalStart = invExons.getIntervalStarts().get(chromosom);
                ArrayList<Long> intervalEnd = invExons.getIntervalsEnd().get(chromosom);

                for(int i = 0; i < invExons.getIntervalStarts().get(chromosom).size(); i++){

                    String s = chromosom + "\t" + intervalStart.get(i) + "\t" + intervalEnd.get(i) + "\n";

                    writer.write(s);

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
