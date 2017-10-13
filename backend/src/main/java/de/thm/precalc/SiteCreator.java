package de.thm.precalc;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.genomeData.tracks.Track;
import de.thm.logo.GenomeFactory;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Fills the index table with random site and their properties
 *
 * Created by menzel on 2/8/17.
 */
class SiteCreator {

    private final Logger logger = LoggerFactory.getLogger(SiteCreator.class);

    /**
     * Creates an indexTable for a given assembly and count of positions
     *
     * @param assembly - assembly number
     * @param count - count of positions to create
     *
     * @return indextable with count positions
     */
    IndexTable create(Genome.Assembly assembly, int count) {

        IndexTable indexTable = new IndexTable();
        Sites model = BackgroundModelFactory.createBackgroundModel(assembly, count);
        List<Long> positions = model.getPositions();
        indexTable.setPositions(positions);
        List<Track> tracks = new ArrayList<>(); //TrackFactory.getInstance().getTracks(assembly); // disabled to save init time

        if(tracks.size() > 0) { //only call threads if there is work to do

            BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(tracks.size());
            ExecutorService exe = new ThreadPoolExecutor(4, 32, 3L, TimeUnit.MINUTES, queue);

            // fill for all basic tracks
            for (Track track : tracks) exe.execute(new PropWrapper(track, positions, indexTable));

            exe.shutdown();

            try {
                exe.awaitTermination(2, TimeUnit.MINUTES);

            } catch (InterruptedException e) {
                logger.error("Exception {}", e.getMessage(), e);
                exe.shutdownNow();
            } finally {
                if (!exe.isTerminated())
                    logger.warn("Killing all precalc tasks now");
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
            long[] ends = track.getEnds();

            for (int i = 0; i < ends.length; i++) {
                Long end = ends[i];

                if (end <= pos)
                    continue;

                Long start = track.getStarts()[i];

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
    private List<String> fill_sequence(Genome.Assembly assembly, List<Long> positions) {
        return GenomeFactory.getInstance().getSequence(assembly, positions, 12, Integer.MAX_VALUE);
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
            //logger.info("loaded " + track.getName());
        }
    }
}

