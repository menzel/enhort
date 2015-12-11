package de.thm.bootstrap;

import de.thm.calc.Intersect;
import de.thm.calc.IntersectSimple;
import de.thm.calc.Result;
import de.thm.genomeData.IntervalNamed;
import de.thm.positionData.SimpleBackgroundModel;
import de.thm.positionData.Sites;
import de.thm.stat.QuiSquareTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Michael Menzel on 11/12/15.
 */
public class Analyse {

    private IntervalNamed invGenes;
    private IntervalNamed invHmm;
    private IntervalNamed invConservation;
    private Intersect simple;

    public Analyse() {

        invGenes = new IntervalNamed(new File("/home/menzel/Desktop/THM/lfba/projekphase/knownGenes.bed"));
        //invHmm = new IntervalNamed(new File("/home/menzel/Desktop/THM/lfba/projekphase/hmm.bed"));
        invConservation = new IntervalNamed(new File("/home/menzel/Desktop/THM/lfba/projekphase/conservation.bed"));

        simple = new IntersectSimple();

    }

    public void analyseAll(Sites userSites){

        Sites bg = new SimpleBackgroundModel(userSites.getPositionCount());

        Result userResult = simple.searchSingleIntervall(invGenes,userSites);
        Result bgResult = simple.searchSingleIntervall(invGenes,bg);

        System.out.println("" + "\t\t" + "in" + "\t" + "out");
        System.out.println("user:\t" + userResult.getA() + "\t" + userResult.getB());
        System.out.println("bg:\t\t" + bgResult.getA() + "\t" + bgResult.getB());

        System.out.println("P-value: " +  QuiSquareTest.chiSquareTest(userResult, bgResult));


        // conservation :

        userResult = simple.searchSingleIntervall(invConservation,userSites);
        bgResult = simple.searchSingleIntervall(invConservation,bg);


        try(BufferedWriter writer = Files.newBufferedWriter(new File("/home/menzel/Desktop/THM/lfba/projekphase/R-eval/user").toPath())){
            writer.write(userResult.getResultScores());

        } catch (IOException e) {
            e.printStackTrace();
        }
        try(BufferedWriter writer = Files.newBufferedWriter(new File("/home/menzel/Desktop/THM/lfba/projekphase/R-eval/bg").toPath())){
            writer.write(bgResult.getResultScores());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void benchmark(){

        Intersect simple = new IntersectSimple();

        for(int i = 0 ; i < 20 ; i++){
            int j = i * 5000;

            Sites bg = new SimpleBackgroundModel(j);
            long startTime = System.nanoTime();

            //simple.searchSingleIntervall(invHmm,bg);
            simple.searchSingleIntervall(invHmm, bg);

            long duration = System.nanoTime() - startTime;
            System.out.print(duration/1000000 + "\t");


            startTime = System.nanoTime();
            //secBinaryNamed.searchSingleIntervall(invHmm,bg);
            //secBinaryNamed.searchSingleIntervall(invHmm, bg);


            duration = System.nanoTime() - startTime;
            System.out.println(duration/1000000);
        }

    }

}
