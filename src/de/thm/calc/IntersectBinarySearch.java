package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;

import java.util.ArrayList;

/**
 * Created by Michael Menzel on 9/12/15.
 */
public class IntersectBinarySearch implements Intersect {

    @Override
    public Result searchSingleIntervall(Interval intv, Sites pos) {
        int in = 0;
        int out = 0;


        for(String chromosom: pos.getPositions().keySet()){
            int last_start = 0;

            for(Long p: pos.getPositions().get(chromosom)){

                ArrayList<Long> intervals = intv.getIntervals().get(chromosom);

                int mid = (int) ((intervals.size() * ((double)p / (double) ChromosomSizes.getChrSize(chromosom))));
                mid = (mid >= intervals.size()) ? intervals.size()-1: mid;

                int end = intervals.size()-1;
                int start = last_start;
                int iSize = intervals.size();

                while(start <= end) {
                    long midElement = intervals.get(mid);

                    if(p < midElement && (mid != 0 && p < intervals.get(mid-1))) end = mid - 1;
                    else if(p > midElement && ( mid != iSize-1) && p > intervals.get(mid+1)) start = mid + 1;
                    else {
                        in++;
                        last_start = mid-1;


                        /*
                        if(p < intervals.get(mid)){
                            if(mid%2 == 0){
                                out++;
                            } else{
                                in++;
                            }

                        } else{
                             if(mid%2 == 0){
                                in++;
                            } else{
                                out++;
                            }
                        }
                        */

                        break;
                    }

                    mid = start + (end - start) / 2;

                }

            }

        }

        return new Result(in, out);
    }
}
