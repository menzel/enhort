package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.ArrayList;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class IntersectSimple implements Intersect{

    public Result searchSingleIntervall(Interval intv, Sites pos){
        int out = 0;
        int in = 0;
        Result result = new Result();
        result.setUsedInterval(intv);

        for(String chromosom: pos.getPositions().keySet()){
            long c;
            int i = 0;

            ArrayList<Long> intervalStart = intv.getIntervalStarts().get(chromosom);
            ArrayList<Long> intervalEnd = intv.getIntervalsEnd().get(chromosom);
            ArrayList<String> intervalName = intv.getIntervalName().get(chromosom);
            ArrayList<Long> intervalScore = intv.getIntervalScore().get(chromosom);

            int intervalCount = intervalStart.size();

            for(Long p: pos.getPositions().get(chromosom)){


                for(; i < intervalCount; i++){

                    c = intervalStart.get(i);

                    if(p < c || i == intervalCount-1){
                        if(i != 0 && p <= intervalEnd.get(i-1)){

                            in++;

                            result.add(intervalName.get(i-1));
                            result.add(intervalScore.get(i-1));

                        }else{
                            out++;
                        }

                        break;
                    }
                }
            }
        }

        result.add("out", out);
        result.setIn(in);

        return result;
    }
}