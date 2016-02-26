package de.thm.calc;

import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.ScoredTrack;
import de.thm.genomeData.Track;
import de.thm.positionData.Sites;

import java.util.List;
/**
 * Simple version of intersect, going list by list.
 */
public final class IntersectCalculate<T extends Track> implements Intersect<T>{


    @Override
    public IntersectResult searchSingleInterval(T intv, Sites pos) {
        if(intv instanceof InOutTrack)
            return searchSingleInterval((InOutTrack)intv, pos);
        if(intv instanceof ScoredTrack)
            return searchSingleInterval((ScoredTrack)intv, pos);
        else
            return null;
    }

    public IntersectResult searchSingleInterval(InOutTrack intv, Sites pos){
        int out = 0;
        int in = 0;
        int i = 0;

        IntersectResult intersectResult = new IntersectResult(intv);

        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();

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

                } else{
                    out++;
                }
            }else{
                if(p >= intervalEnd.get(i-1)){
                    out++;

                }else{
                    in++;
                }
            }
        }

        intersectResult.setOut(out);
        intersectResult.setIn(in);

        return intersectResult;
    }



    public IntersectResult searchSingleInterval(ScoredTrack intv, Sites pos){

        int out = 0;
        int in = 0;
        int i = 0;

        IntersectResult intersectResult = new IntersectResult(intv);

        List<Long> intervalStart = intv.getIntervalsStart();
        List<Long> intervalEnd = intv.getIntervalsEnd();
        List<Double> intervalScore = intv.getIntervalScore();

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
                    intersectResult.add(intervalScore.get(i-1));

                } else{
                    out++;
                }
            }else{
                if(p >= intervalEnd.get(i-1)){
                    out++;

                }else{
                    in++;
                    intersectResult.add(intervalScore.get(i-1));
                }
            }
        }

        intersectResult.setOut(out);
        intersectResult.setIn(in);

        return intersectResult;
    }

}