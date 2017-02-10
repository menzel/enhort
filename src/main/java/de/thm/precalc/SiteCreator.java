package de.thm.precalc;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
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
    private ExecutorService exe;

    IndexTable create(GenomeFactory.Assembly assembly, int count) {

        List<Track> tracks = TrackFactory.getInstance().getTracks(assembly);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(tracks.size());
        exe = new ThreadPoolExecutor(4, 32, 59L, TimeUnit.SECONDS, queue);

        IndexTable indexTable = new IndexTable();

        Sites model = BackgroundModelFactory.createBackgroundModel(assembly, count);
        List<Long> positions = model.getPositions();
        indexTable.setPositions(positions);

        // fill for all basic tracks
        for(Track track: tracks){
            PropWrapper propWrapper = new PropWrapper(track, positions, indexTable);
            exe.execute(propWrapper);
        }


        exe.shutdown();

        try {
            exe.awaitTermination(2, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            e.printStackTrace();
            exe.shutdownNow();
        } finally {
            if(!exe.isTerminated())
                System.err.println("Killing all precalc tasks now");
            exe.shutdownNow();
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

        private Track track;
        private List<Long> positions;
        private IndexTable indexTable;

        PropWrapper(Track track, List<Long> positions, IndexTable indexTable) {

            this.track = track;
            this.positions = positions;
            this.indexTable = indexTable;
        }

        @Override
        public void run() {
            indexTable.setProperties(track,fill_inout(positions, track));
            System.out.println("loaded " + track.getName());
        }
    }
}

