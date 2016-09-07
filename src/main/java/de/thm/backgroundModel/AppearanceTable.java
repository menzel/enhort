package de.thm.backgroundModel;

import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.positionData.Sites;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class holds the appearances for the different combinations of points in intervals
 * <p>
 * Created by Michael Menzel on 13/1/16.
 */
class AppearanceTable {

    private Map<String, Integer> appearance;
    private int minSites;

    AppearanceTable(int minSites) {
        this.minSites = minSites;
    }


    /**
     * Puts the appearances in the appearance table. Each point is added to one of the hash values, depending if it is in none, one or more intervals
     *
     * @param positions - positions to find in intervals
     * @param tracks    - intervals to match against
     */
    void fillTable(List<Track> tracks, Sites positions) {
        appearance = new HashMap<>();
        Map<Track, Integer> indices = new HashMap<>();

        //init indices map:
        for (Track track : tracks) {
            indices.put(track, 0);
        }


        for (Long p : positions.getPositions()) {
            Set<Integer> containing = new TreeSet<>();

            for (Track track : tracks) {

                List<Long> intervalStart = track.getIntervalsStart();
                List<Long> intervalEnd = track.getIntervalsEnd();

                int i = indices.get(track);
                int intervalCount = intervalStart.size() - 1;

                while (i < intervalCount && intervalEnd.get(i) <= p)
                    i++;

                if(i == intervalCount && p >= intervalEnd.get(i)) { //not inside last interval
                    break; //end the loop over all positions
                }

                if (p >= intervalStart.get(i)){
                    containing.add(track.getUid());
                }

                indices.put(track, i);
            }

            if (appearance.containsKey(hash(containing))) {
                appearance.put(hash(containing), appearance.get(hash(containing)) + 1);
            } else {
                appearance.put(hash(containing), 1);
            }
        }


        int sum = appearance.values().stream().mapToInt(i->i).sum();

        if(sum < minSites){
            int factor = (minSites/ sum);
            factor *= 1.2;

            for(Map.Entry<String, Integer> set:appearance.entrySet()){
                appearance.put(set.getKey(), set.getValue() * factor);
            }
        }
    }

    /**
     * Gets the hash key for a list of Interval Ids.
     *
     * @param containing - list of interval id's
     * @return HashMap key as String
     */
    String hash(Set<Integer> containing) {

        List<Integer> list = new ArrayList<>(containing);
        Collections.sort(list);

        return Arrays.toString(list.toArray());
    }


    /**
     * Gets the hash key for a list of Interval Ids.
     *
     * @param tracks - list of intervals
     * @return HashMap key as String
     */
    String hash(List<Track> tracks) {
        List<Integer> containing = new ArrayList<>();

        for (Track track : tracks) {
            containing.add(track.getUid());
        }

        Collections.sort(containing);

        return Arrays.toString(containing.toArray());
    }

    Set<String> getKeySet() {
        return appearance.keySet();
    }

    /**
     * Get the appearance for a list of intervals
     *
     * @param tracks - intervals to fetch the value from
     * @return appearance count
     */
    int getAppearance(List<Track> tracks) {

        if (appearance.containsKey(hash(tracks))) {
            return appearance.get(hash(tracks));

        } else {
            return 0;
        }
    }

    /**
     * Get the appearance for a String key.
     *
     * @param app - Hash map key
     * @return appearance count
     */
    int getAppearance(String app) {
        if (!appearance.containsKey(app))
            return 0;
        return appearance.get(app);
    }

    /**
     * Translates a hash key (String) app to a list of intervals.
     * This method is for testing environment, use translate(String) for other
     *
     * @param app - hash key as String
     * @return List of intervals which are referenced in the hash key
     */
    List<Track> translate(String app, List<Track> knownTracks) {

        if (app.compareTo("[]") == 0) { //empty array
            return null;
        }

        List<Track> tracks = new ArrayList<>();

        app = app.substring(1, app.length() - 1);

        int[] digits = Arrays.stream(app.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();

        for (int id : digits) {
            for (Track track : knownTracks) {
                if (id == track.getUid())
                    tracks.add(track);
            }
        }


        return tracks;
    }

    /**
     * Translates a String of Interval Ids to a list of intervals.
     *
     * @param app - appearance hash key as string
     * @return List of Intervals
     */
    public List<Track> translate(String app) {

        if (app.compareTo("[]") == 0) { //empty array
            return null;
        }

        List<Track> tracks = new ArrayList<>();
        TrackFactory loader = TrackFactory.getInstance();

        app = app.substring(1, app.length() - 1);

        int[] digits = Arrays.stream(app.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();

        for (int id : digits) {
            tracks.add(loader.getIntervalById(id));
        }


        return tracks;
    }

    /**
     * Returns all intervals exepct the ones given by param
     *
     * @param outer - all intervals which were selected for bg
     * @param app   - string from Arrays.toString() [1,2,3,..] as key
     * @return list of all intervals exepect the ones on app list of interval ids.
     */
    List<Track> translateNegative(List<Track> outer, String app) {

        List<Track> tracks = new CopyOnWriteArrayList<>(outer);

        app = app.substring(1, app.length() - 1);

        int[] digits = Arrays.stream(app.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();


        //remove those which are in the given list
        for (int id : digits) {
            for (Track track : tracks) {
                if (track.getUid() == id)
                    tracks.remove(track);
            }
        }

        return tracks;
    }

    public Map<String, Integer> getAppearance() {
        return appearance;
    }

    void setAppearance(Map<String, Integer> appearance) {
        this.appearance = appearance;
    }
}
