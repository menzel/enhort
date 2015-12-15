package de.thm.bootstrap;

import de.thm.calc.Intersect;
import de.thm.calc.IntersectSimple;
import de.thm.calc.Result;
import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalNamed;
import de.thm.positionData.SimpleBackgroundModel;
import de.thm.positionData.Sites;
import de.thm.stat.IndependenceTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Menzel on 11/12/15.
 */
public class Analyse {

    private Intersect simple;
    private Map<String, Interval> intervals;

    public Analyse() {
        intervals = new HashMap<>();
        String basePath = "/home/menzel/Desktop/THM/lfba/projekphase/dat/";

        try {

            getIntervals(basePath + "inout", Interval.Type.inout);
            getIntervals(basePath + "named", Interval.Type.named);
            getIntervals(basePath + "score", Interval.Type.score);

        } catch (IOException e) {
            e.printStackTrace();
        }

        simple = new IntersectSimple();
    }

    private void getIntervals(String path, Interval.Type type) throws IOException {

        Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach(filePath -> {
            intervals.put(filePath.getFileName().toString(), new IntervalNamed(filePath.toFile(), type));
        });
    }

    public void analyse(Sites userSites){

        Sites bg = new SimpleBackgroundModel(userSites.getPositionCount());

        Result resultUserSites;
        Result resultBg;

        // H_0: bg and user sites are independent. Large pValue: bg and user are independent. Small pValue: bg and user are dependent.

        for(String intervalName: intervals.keySet()){
            resultUserSites = simple.searchSingleIntervall(intervals.get(intervalName), userSites);
            resultBg = simple.searchSingleIntervall(intervals.get(intervalName), bg);

            IndependenceTest tester = new IndependenceTest();
            double pValue = tester.test(resultUserSites, resultBg);
            System.out.println(intervalName + " p-value: " + pValue);
        }

    }



    /*
    public void foo(Sites userSites){

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
            //simple.searchSingleIntervall(invHmm, bg);

            long duration = System.nanoTime() - startTime;
            System.out.print(duration/1000000 + "\t");


            startTime = System.nanoTime();
            //secBinaryNamed.searchSingleIntervall(invHmm,bg);
            //secBinaryNamed.searchSingleIntervall(invHmm, bg);


            duration = System.nanoTime() - startTime;
            System.out.println(duration/1000000);
        }

    }
    */

}
