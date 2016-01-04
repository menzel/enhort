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
            int i = 0;
            int pCount = 0;

            ArrayList<Long> intervalStart = intv.getIntervalStarts().get(chromosom);
            ArrayList<Long> intervalEnd = intv.getIntervalsEnd().get(chromosom);
            ArrayList<String> intervalName = intv.getIntervalName().get(chromosom);
            ArrayList<Long> intervalScore = intv.getIntervalScore().get(chromosom);

            int intervalCount = intervalStart.size()-1;

            for(Long p: pos.getPositions().get(chromosom)) {
                pCount++;

                while(i < intervalCount && intervalStart.get(i) <= p){
                    i++;
                }

                if(i == 0){
                    out++;

                } else if(i == intervalCount){ //last Interval
                    if(p <= intervalEnd.get(i)){

                        in++;
                        result.add(intervalName.get(i));
                        if(intv.getType() == Interval.Type.score)
                                result.add(intervalScore.get(i-1));

                    } else{
                        // the remaining positions cannot be in an interval.
                        int size =  pos.getPositions().get(chromosom).size();
                        out += 1 + size - pCount;  // adding count of positions left to out
                        break;

                    }
                }else{
                    if(p > intervalEnd.get(i-1)){
                        out++;

                    }else{

                        in++;
                        result.add(intervalName.get(i));
                        if(intv.getType() == Interval.Type.score)
                                result.add(intervalScore.get(i-1));

                    }
                }

            }
        }

        result.add("out", out);
        result.setIn(in);

        return result;
    }
}