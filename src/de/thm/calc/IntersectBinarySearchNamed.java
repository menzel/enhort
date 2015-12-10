package de.thm.calc;

import de.thm.genomeData.IntervalNamed;
import de.thm.misc.ChromosomSizes;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Menzel on 9/12/15.
 */
public class IntersectBinarySearchNamed {

    public Map<String, Integer> searchSingleIntervall(IntervalNamed intv, Sites pos) {
        Map<String, Integer> result = new HashMap<>();


        for(String chromosom: pos.getPositions().keySet()){
            int last_start = 0;

            ArrayList<Long> intervalStart = intv.getIntervalStarts().get(chromosom);
            ArrayList<Long> intervalEnd = intv.getIntervalsEnd().get(chromosom);
            int iSize = intervalStart.size();

            for(Long p: pos.getPositions().get(chromosom)){

                int mid = (int) ((intervalStart.size() * ((double)p / (double) ChromosomSizes.getChrSize(chromosom))));

                int end = intervalStart.size()-1;
                int start = last_start;

                while(start <= end) {

                    long midElement = intervalStart.get(mid);
                    long previous = (mid != 0) ? intervalStart.get(mid-1): 0;
                    long next = (mid != iSize-1) ? intervalStart.get(mid+1): iSize-1;

                    if(p < midElement && mid != 0 && p < previous) end = mid - 1;
                    else if(p > midElement && mid < iSize-1 && p > next) start = mid + 1;
                    else{

                         if(p < midElement){
                            if(mid != 0 && intervalEnd.get(mid-1) > p) { //in previous interval
                                mid = mid-1;
                            }
                        }

                        String hmmName = intv.getIntervalName().get(chromosom).get(mid);

                        //TODO
                        if(result.containsKey(hmmName)){
                            result.put(hmmName,result.get(hmmName)+1);
                        }else{
                            result.put(hmmName,1);
                        }

                        break;
                    }

                    mid = start + (end - start) / 2;
                }
            }
        }

        return result;
    }
}
