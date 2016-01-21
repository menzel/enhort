package de.thm.backgroundModel;

import de.thm.genomeData.Interval;
import de.thm.genomeData.IntervalLoader;
import de.thm.positionData.Sites;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Michael Menzel on 13/1/16.
 */
public class AppearanceTable {

    private Map<String, Integer> appearance;


    /**
     *
     * @param intervals
     */
    public void fillTable(List<Interval> intervals, Sites positions) {
        appearance = new HashMap<>();
        Map<Interval, Integer> indices = new HashMap<>();

        //init indices map:
        for(Interval interval: intervals) {
            indices.put(interval, 0);
        }


        for(Long p : positions.getPositions()){
            Set<Integer> containing = new TreeSet<>();

            for(Interval interval: intervals){

                List<Long> intervalStart = interval.getIntervalsStart();
                List<Long> intervalEnd = interval.getIntervalsEnd();

                int i = indices.get(interval);
                int intervalCount = intervalStart.size()-1;

                while(i < intervalCount && intervalStart.get(i) <= p){
                    i++;
                }

                if(i == 0){
                    if(p >= intervalStart.get(i)){
                        containing.add(interval.getUid());
                    }

                } else if(i == intervalCount && p > intervalEnd.get(i-1)){ //last Interval and p not in previous
                    if(p < intervalEnd.get(i) && p >= intervalStart.get(i)){

                        containing.add(interval.getUid());

                    } else{
                        continue;
                    }
                }else{
                    if(p >= intervalEnd.get(i-1)){
                        continue;

                    }else{
                        containing.add(interval.getUid());
                    }
                }

                indices.put(interval, i);
            }

            if(appearance.containsKey(hash(containing))){
                appearance.put(hash(containing), appearance.get(hash(containing))+1);
            }else{
                appearance.put(hash(containing), 1);
            }
        }
    }

    protected String hash(Set<Integer> containing) {
        long r = 0;
        int i = 0;

        List<Integer> list = new ArrayList<>(containing);
        Collections.sort(list);

        return Arrays.toString(list.toArray());
    }

    protected String hash(List<Interval> intervals) {
        List<Integer> containing = new ArrayList<>();

        for(Interval interval: intervals){
            containing.add(interval.getUid());
        }

        Collections.sort(containing);

        return Arrays.toString(containing.toArray());
    }

    public Set<String> getKeySet(){
        return appearance.keySet();
    }

    /**
     *
     * @param intervals
     * @return
     */
    public int getAppearance(List<Interval> intervals){
        //System.out.println("given interval ids: " +intervals.stream().map(Interval::getUid).collect(Collectors.toList()));
        //System.out.println("appearance hash: " +Arrays.toString(appearance.keySet().toArray()));
        //System.out.print("Hash function of given interval ids " + hash(intervals));
        //System.out.println("\n_______");

        if(appearance.containsKey(hash(intervals))){
            return appearance.get(hash(intervals));

        } else{
            return 0;
        }
    }

    public int getAppearance(String app) {
        if(app.equals("[]") && !appearance.containsKey("[]"))
            return 0;
        return appearance.get(app);
    }

    /**
     *
     * @param app
     * @return
     */
    public List<Interval> translate(String app, List<Interval> knownIntervals) {

        if(app.compareTo("[]") == 0){ //empty array
            return null;
        }

        List<Interval> intervals = new ArrayList<>();

        app = app.substring(1, app.length()-1);

        int[] digits =  Arrays.stream(app.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();

        for (int id : digits) {
            for(Interval interval: knownIntervals){
                if(id == interval.getUid())
                    intervals.add(interval);
            }
        }


        return intervals;
    }

    /**
     *
     * @param app
     * @return
     */
    public List<Interval> translate(String app) {

        if(app.compareTo("[]") == 0){ //empty array
            return null;
        }

        List<Interval> intervals = new ArrayList<>();
        IntervalLoader loader = IntervalLoader.getInstance();

        app = app.substring(1, app.length()-1);

        int[] digits =  Arrays.stream(app.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();

        for (int id : digits) {
            intervals.add(loader.getIntervalById(id));
        }


        return intervals;
    }

    /**
     * Returns all intervals exepct the ones given by param
     *
     *
     * @param outer - all intervals which were selected for bg
     * @param app - string from Arrays.toString() [1,2,3,..] as key
     *
     * @return list of all intervals exepect the ones on app list of interval ids.
     */
    public List<Interval> translateNegative(List<Interval> outer, String app) {

        List<Interval> intervals = new CopyOnWriteArrayList<>(outer);

        app = app.substring(1, app.length()-1);

        int[] digits =  Arrays.stream(app.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();


        //remove those which are in the given list
        for (int id : digits) {
            for(Interval interval: intervals){
                if(interval.getUid() == id)
                    intervals.remove(interval);
            }
        }

        return intervals;
    }

    public Map<String, Integer> getAppearance() {
        return appearance;
    }

    public void setAppearance(Map<String, Integer> appearance) {
        this.appearance = appearance;
    }
}
