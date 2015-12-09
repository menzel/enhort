package de.thm.calc;

import de.thm.genomeData.IntervalDual;
import de.thm.positionData.Sites;

import java.util.ArrayList;

/**
 * Created by Michael Menzel on 9/12/15.
 */
public class IntersectDual {

    public Result searchSingleIntervall(IntervalDual intv, Sites pos) {
        int in = 0;
        int out = 0;

        for (String chromosom : pos.getPositions().keySet()) {
            long c;
            int i = 0;

            ArrayList<Long> intervalStart = intv.getIntervalStarts().get(chromosom);
            ArrayList<Long> intervalEnd = intv.getIntervalsEnd().get(chromosom);

            for (Long p : pos.getPositions().get(chromosom)) {


                int intervallCount = intervalStart.size();

                for (; i < intervallCount; i++) {


                    long f = intervalStart.get(i);

                    if(p < intervalStart.get(i)){

                        long endBefore = (i == 0)? 0 :intervalEnd.get(i-1);

                        if(p < endBefore){
                            in++;
                        }else{
                            out++;
                        }
                        break;
                    }
                }
            }
        }
        return new Result(in,out);
    }
}
