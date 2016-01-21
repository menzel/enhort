package de.thm.calc;

import de.thm.genomeData.Interval;
import de.thm.positionData.Sites;

import java.util.List;

/**
 * Simple version of intersect, going list by list.
 */
public class IntersectCalculate implements Intersect{

    private IntersectResult intersectResult;


    public IntersectCalculate() {
    }


    public IntersectResult searchSingleInterval(Interval intv, Sites pos){

        int out = 0;
        int in = 0;
        int i = 0;

        IntersectResult intersectResult = new IntersectResult();
        intersectResult.setUsedInterval(intv);

        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();
        List<String> intervalName = intv.getIntervalName();
        List<Long> intervalScore = intv.getIntervalScore();

        int intervalCount = intervalStart.size()-1;


        for(Long p: pos.getPositions()) {

            while(i < intervalCount && intervalStart.get(i) <= p){
                i++;
            }

            if(i == 0){
                out++;

            } else if(i == intervalCount && p > intervalEnd.get(i-1)){ //last Interval and p not in previous
                if(p < intervalEnd.get(i) && p >= intervalStart.get(i)){

                    in++;

                    if(intv.getType() == Interval.Type.named)
                        intersectResult.add(intervalName.get(i));
                    if(intv.getType() == Interval.Type.score)
                            intersectResult.add(intervalScore.get(i-1));
                } else{
                    out++;
                }
            }else{
                if(p >= intervalEnd.get(i-1)){
                    out++;

                }else{
                    in++;

                    if(intv.getType() == Interval.Type.named)
                        intersectResult.add(intervalName.get(i));
                    if(intv.getType() == Interval.Type.score)
                            intersectResult.add(intervalScore.get(i-1));
                }
            }
        }

        intersectResult.add("out", out);
        intersectResult.setIn(in);

        return intersectResult;
    }

    public IntersectResult getIntersectResult() {
        return intersectResult;
    }
}