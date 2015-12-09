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

        System.out.println(pos.getPositionCount());


        for(String chromosom: pos.getPositions().keySet()){
            long c;
            int i = 0;

            for(Long p: pos.getPositions().get(chromosom)){

               ArrayList<Long> intervals = intv.getIntervals().get(chromosom);
               int intervallCount = intervals.size();

               for(; i < intervallCount; i++){

                   c = intervals.get(i);

                   if(p < c){
                       //get one back to get interval start/stop

                       if((i-1)%2 == 0){ //last position was start
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

