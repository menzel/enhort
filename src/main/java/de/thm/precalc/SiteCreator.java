package de.thm.precalc;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.genomeData.Track;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Fills the index table with random site and their properties
 *
 * Created by menzel on 2/8/17.
 */
class SiteCreator {

    /**
     * Creates an indexTable for a given assembly and count of positions
     *
     * @param assembly - assembly number
     * @param count - count of positions to create
     *
     * @return indextable with count positions
     */
    IndexTable create(GenomeFactory.Assembly assembly, int count) {

        IndexTable indexTable = new IndexTable();
        Sites model = BackgroundModelFactory.createBackgroundModel(assembly, count);
        List<Long> positions = model.getPositions();
        indexTable.setPositions(positions);
        List<Track> tracks = new ArrayList<>(); //TrackFactory.getInstance().getTracks(assembly);

        if(tracks.size() > 0) { //only call threads if there is work to do

            BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(tracks.size());
            ExecutorService exe = new ThreadPoolExecutor(4, 32, 59L, TimeUnit.SECONDS, queue);

            // fill for all basic tracks
            for (Track track : tracks) exe.execute(new PropWrapper(track, positions, indexTable));

            exe.shutdown();

            try {
                exe.awaitTermination(2, TimeUnit.MINUTES);

            } catch (InterruptedException e) {
                e.printStackTrace();
                exe.shutdownNow();
            } finally {
                if (!exe.isTerminated())
                    System.err.println("Killing all precalc tasks now");
                exe.shutdownNow();
            }
        }

        // fill sequences
        indexTable.setSequences(fill_sequence(assembly,positions));

        return indexTable;
    }




    /**
     *
     * Fills the list of booleans (for In/Out Tracks) for observed positions
     *
     * @param positions - positions to look up
     * @param track - InOutTrack to check
     */
    private List<Integer> fill_inout(List<Long> positions, Track track) {

        List<Integer> inout = new ArrayList<>();

        for(Long pos: positions){
            List<Long> ends = track.getEnds();

            for (int i = 0; i < ends.size(); i++) {
                Long end = ends.get(i);

                if (end <= pos)
                    continue;

                Long start = track.getStarts().get(i);

                if(start <= pos)
                    inout.add(1);
                else
                    inout.add(0);

                break; // break loop over ends
            }
        }

        return inout;
    }

       /**
     * Fills the sequence list of the stored positions
     *
     * @param positions - positions to look up
     */
    private List<String> fill_sequence(GenomeFactory.Assembly assembly, List<Long> positions) {
        return GenomeFactory.getInstance().getSequence(assembly, positions, 9, Integer.MAX_VALUE);
    }

    private class PropWrapper implements Runnable{

        private final Track track;
        private final List<Long> positions;
        private final IndexTable indexTable;

        PropWrapper(Track track, List<Long> positions, IndexTable indexTable) {

            this.track = track;
            this.positions = positions;
            this.indexTable = indexTable;
        }

        @Override
        public void run() {
            indexTable.setProperties(track,fill_inout(positions, track));
            //System.out.println("loaded " + track.getName());
        }
    }
}

