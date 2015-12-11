package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.ArrayList;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class IntersectSimple implements Intersect{

    public Result searchSingleIntervall(Interval intv, Sites pos){
        int in = 0;
        int out = 0;

        for(String chromosom: pos.getPositions().keySet()){
            long c;
            int i = 0;

            ArrayList<Long> intervalStart = intv.getIntervalStarts().get(chromosom);
            ArrayList<Long> intervalEnd = intv.getIntervalsEnd().get(chromosom);
            int intervalCount = intervalStart.size();

            for(Long p: pos.getPositions().get(chromosom)){


                for(; i < intervalCount; i++){

                    c = intervalStart.get(i);

                    if(p < c || i == intervalCount-1){
                        long f = intervalEnd.get(i);
                        if(i != 0 && p <= intervalEnd.get(i-1)){
                            in++;
                        }else{
                            out++;
                        }

                        break;
                    }
                }
            }
        }

        Result result = new Result();
        result.add("in", in);
        result.add("out", out);

        return result;
    }
}