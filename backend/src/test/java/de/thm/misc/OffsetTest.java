package de.thm.misc;

import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackEntry;
import de.thm.genomeData.tracks.TrackFactory;
import org.junit.Test;

public class OffsetTest {

    @Test
    public void testOffset() throws Exception {

        TrackFactory tf = TrackFactory.getInstance();

        TrackEntry entry = new TrackEntry("foo", "desc", "hg19/inout/contigs", "inout", "hg19", "", 20, "", 42);
        tf.loadTrack(entry);

        Track track = tf.getTrackByName("foo", Genome.Assembly.hg19);


        for(int i = 0; i < track.getStarts().length; i++){
            System.out.println(track.getStarts()[i] + "\t" + track.getEnds()[i]);
        }
    }
}
