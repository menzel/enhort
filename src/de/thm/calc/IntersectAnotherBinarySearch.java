package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;

import java.util.ArrayList;

/**
 * Created by Michael Menzel on 9/12/15.
 */
public class IntersectAnotherBinarySearch implements Intersect {

    @Override
    public Result searchSingleIntervall(Interval intv, Sites pos) {
        int in = 0;
        int out = 0;


        for(String chromosom: pos.getPositions().keySet()){
            int last_start = 0;

            for(Long p: pos.getPositions().get(chromosom)){

                ArrayList<Long> intervalStart = intv.getIntervalStarts().get(chromosom); //TODO use both lists
                ArrayList<Long> intervalEnd = intv.getIntervalsEnd().get(chromosom); //TODO use both lists

                int mid = (int) ((intervalStart.size() * ((double)p / (double) ChromosomSizes.getChrSize(chromosom))));
                /*
                mid = (mid >= intervalStart.size()) ? intervalStart.size()-1: mid;
                mid = (mid >= 0)? mid: 0;
                */

                int end = intervalStart.size()-1;
                int start = last_start;
                int iSize = intervalStart.size();

                while(start <= end) {

                    long midElement = intervalStart.get(mid);
                    long previous = (mid != 0) ? intervalStart.get(mid-1): 0;
                    long next = (mid != iSize-1) ? intervalStart.get(mid+1): iSize-1;

                    if(p < midElement && mid != 0 && p < previous) end = mid - 1;
                    else if(p > midElement && mid < iSize-1 && p > next) start = mid + 1;
                    else{

                        if(p < midElement){
                            if(mid != 0 && intervalEnd.get(mid-1) > p) { //in previous interval
                                in++;
                            }else{
                                out++;
                            }
                        }else{
                            if(intervalEnd.get(mid) > p){
                                in++;
                            }else{
                               out++;
                            }
                        }
                        break;
                    }

                    mid = start + (end - start) / 2;
                    mid = (mid >= 0)? mid: 0;
                }


                if(start > end) out++;

            }

        }

        return new Result(in, out);
    }
}
