package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class IntersectSimple implements Intersect{

    public Result searchSingleIntervall(Interval intv, Sites pos){
        int in = 0;
        int out = 0;


        for(String chromosom: pos.getPositions().keySet()){
            long c = 0;

            for(Long p: pos.getPositions().get(chromosom)){

               for(int i = 0; i < intv.getIntervals().get(chromosom).size(); i++){

                   c = intv.getIntervals().get(chromosom).get(i);

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

