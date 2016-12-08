package de.thm.backgroundModel;

import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.positionData.Sites;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Holds a list of precalculated sites together with the attributes for each position for fast creation of background models
 *
 * Created by menzel on 12/6/16.
 */
public class PrecalcSiteFactory {

    static List<Long> positions = new ArrayList<>();
    private static GenomeFactory.Assembly assembly;
    private static Map<Long, List<Boolean>> booleans = new HashMap<>();
    private static Map<Long, List<Long>> distances = new HashMap<>();
    private static Map<Long, String> sequence = new HashMap<>();

    /**
     *
     * Generates the initial set of positions
     *
     * @param assembly - assembly number
     * @param count - count of positions to generate
     */
    public static void generatePositions(GenomeFactory.Assembly assembly, int count){
        PrecalcSiteFactory.assembly = assembly;

        //get random positions
        RandomBackgroundModel model = new RandomBackgroundModel(assembly, count);
        positions = model.getPositions();

        for(Long pos: positions)
            booleans.put(pos, new ArrayList<>(Collections.nCopies(3, false)));

        // fill maps for fast attributes access
        fill_boolean(positions, boolean_keys.GENE, TrackFactory.getInstance().getTrackByName("Known genes"));
        fill_boolean(positions, boolean_keys.EXON, TrackFactory.getInstance().getTrackByName("Exons"));
        fill_boolean(positions, boolean_keys.INTRON, TrackFactory.getInstance().getTrackByName("Introns"));

        fill_sequence(positions);

        fill_distances(positions,distance_keys.TSS, TrackFactory.getInstance().getTrackByName("Distance_to_TSS"));
    }

    /**
     * Fills the list of distances of the generated positions
     *
     * @param positions -  positions to use
     * @param key - distance_key, e.g. Distance from TSS
     * @param track - track to check
     */
    private static void fill_distances(List<Long> positions, distance_keys key, Track track) {
        //TODO
    }

    /**
     * Fills the sequence list of the stored positions
     *
     * @param positions - positions to look up
     */
    private static void fill_sequence(List<Long> positions) {
        List<String> seq = GenomeFactory.getInstance().getSequence(assembly, positions, 9, Integer.MAX_VALUE);

        assert seq != null;
        for (int i = 0; i < seq.size(); i++) {

            String s = seq.get(i);
            sequence.put(positions.get(i), s.toLowerCase());
        }
    }

    /**
     *
     * Fills the list of booleans (for In/Out Tracks) for observed positions
     *
     * @param positions - positions to look up
     * @param key - boolean_key e.g. GENE, INTRON
     * @param track - InOutTrack to check
     */
    private static void fill_boolean(List<Long> positions, boolean_keys key, Track track) {

        for(Long pos: positions){
            List<Long> ends = track.getEnds();

            for (int i = 0; i < ends.size(); i++) {
                Long end = ends.get(i);

                if (end <= pos)
                    continue;

                Long start = track.getStarts().get(i);
                booleans.get(pos).set(key.ordinal(), start <= pos);
                break; // break loop over ends
            }
        }
    }

    /**
     * Returns a random set of sites
     *
     * @param count of the returned sites
     * @return count sites
     */
    public static Sites getSites(int count){

        List<Long> pos_copy = new ArrayList<>(positions);
        Collections.shuffle(pos_copy);

        return mockSites(pos_copy.subList(0, count));
    }

    /**
     * Returns a list of sites that are 'In' for a given key (E.g. Genes, Introns...)
     *
     * @param key - key to check
     * @param count of positions to return
     *
     * @return list of positions that are true for the given key
     */
    public static Sites getSites(boolean_keys key, int count){
        List<Long> pos = new ArrayList<>();

        for (Long p : positions) {
            if(booleans.get(p).get(key.ordinal())) //check if the pos fullfills the key request
                pos.add(p);
            if (pos.size() >= count)
                break;
        }

        return mockSites(pos);
    }

    /**
     * Returns a list of sites that are in the given distance to a site given by key
     *
     * @param key - specifies which attribute to check (e.g. TSS)
     * @param distance - expeced distance
     * @param window - allows a window around the site: Start: distance-(window/2). End: distance+(window/2)
     * @param count - count of positions to return
     * @return - count positions that are in the given distance range to given key
     */
    public static Sites getSites(distance_keys key, long distance, long window, int count){
        List<Long> pos = new ArrayList<>();

        for (Long p : positions) {
            if(Math.abs(distances.get(p).get(key.ordinal()) - distance) < window/2) //check if the pos fullfills the key request
                pos.add(p);
            if (pos.size() >= count)
                break;
        }

        return mockSites(pos);
    }

    /**
     * Returns a list of sites that show a similar logo to the given logo
     *
     * @param logo - logo to check
     * @param distance - max distance between sequence and logo
     * @param count - count of positions to return
     *
     * @return  Sites with a logo similar to given logo
     */
    public static Sites getSites(Logo logo, int distance, int count){

        List<Long> pos = new ArrayList<>();
        String regex = generateLogoRegex(logo, distance);

        System.out.println(regex);
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        for (Long p : positions) {
            if(pattern.matcher(sequence.get(p)).matches())
                pos.add(p);
            if (pos.size() >= count)
                break;
        }

        return mockSites(pos);
    }

    private static String generateLogoRegex(Logo logo, int distance) {

        String consensus = logo.getConsensus().toLowerCase();
        int c = (sequence.get(positions.get(0)).length() - consensus.length())/2; //get count of chars before sequence and after for regex

        String logoRegex = logo.getRegex();
        return "\\w{" + (c-1) + "," + (c+1)  + "}" + logoRegex + "\\w{" + (c-1) + "," + (c+1) +"}";
    }

    /**
     * Makes a Sites object from a list of Positions
     *
     * @param pos  - positions to use
     * @return Site object with pos as positions
     */
    private static Sites mockSites(List<Long> pos) {

        return new Sites() {

            List<Long> positions = pos;

            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {
                return positions;
            }

            @Override
            public void setPositions(List<Long> positions) {}

            @Override
            public int getPositionCount() {
                return positions.size();
            }

            @Override
            public GenomeFactory.Assembly getAssembly() {
                return assembly;
            }
        };
    }

    enum boolean_keys {GENE, EXON, INTRON}

    enum distance_keys {TSS, CANCER_GENE}
}
