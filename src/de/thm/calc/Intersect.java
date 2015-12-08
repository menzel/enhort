package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

/**
 * Created by Michael Menzel on 8/12/15.
 */
public class Intersect {

    int searchSingleIntervall(Interval intv, Sites pos){
        int in = 0;

        if(intv.isIn(pos)){
            in++;
        }

        return in;
    }
}
