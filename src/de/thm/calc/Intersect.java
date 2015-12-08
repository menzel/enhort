package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.Iterator;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class Intersect {

    public int searchSingleIntervall(Interval intv, Sites pos){
        int in = 0;

        for(String chromosom: pos.getPositions().keySet()){
            long c = 0;
            System.out.println("in " + chromosom);

            for(Long p: pos.getPositions().get(chromosom)){

               Iterator<Long> it = intv.getIntervals().get(chromosom).iterator();
               c = it.next();

               for(int i = 0; it.hasNext(); i++){

                   if(p < c){
                       //get one back to get interval start/stop

                       if((i-1)%2 == 0){ //last position was start
                           in++;
                       }

                   }else{
                       it.next();
                   }

               }
            }
        }

        return in;
    }
}
