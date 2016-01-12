package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.ArrayList;

/**
 * Simple version of intersect, going list by list.
 */
public class IntersectSimple implements Intersect{

    private Result result;


    public IntersectSimple() {
    }


    public Result searchSingleInterval(Interval intv, Sites pos){

        int out = 0;
        int in = 0;
        int i = 0;

        Result result = new Result();
        result.setUsedInterval(intv);

        ArrayList<Long> intervalStart = intv.getIntervalsStart();
        ArrayList<Long> intervalEnd = intv.getIntervalsEnd();
        ArrayList<String> intervalName = intv.getIntervalName();
        ArrayList<Long> intervalScore = intv.getIntervalScore();

        int intervalCount = intervalStart.size()-1;


        for(Long p: pos.getPositions()) {

            while(i < intervalCount && intervalStart.get(i) <= p){
                i++;
            }

            if(i == 0){
                out++;

            } else if(i == intervalCount){ //last Interval
                if(p < intervalEnd.get(i) && p >= intervalStart.get(i)){

                    in++;
                    result.add(intervalName.get(i));
                    if(intv.getType() == Interval.Type.score)
                            result.add(intervalScore.get(i-1));
                } else{
                    out++;
                }
            }else{
                if(p >= intervalEnd.get(i-1)){
                    out++;

                }else{
                    in++;
                    result.add(intervalName.get(i));
                    if(intv.getType() == Interval.Type.score)
                            result.add(intervalScore.get(i-1));
                }
            }
        }

        result.add("out", out);
        result.setIn(in);

        return result;
    }

    public Result getResult() {
        return result;
    }
}