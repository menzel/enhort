package de.thm.misc;

import de.thm.genomeData.sql.DBConnector;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.Genome;
import org.junit.Test;

public class OffsetTest {

    @Test
    public void testOffset() throws Exception {

        TrackFactory tf = TrackFactory.getInstance();
        DBConnector.TrackEntry entry = (new DBConnector()).createTrackEntry("foo", "", "hg19/inout/contigs", "inout", "hg19", "", 20);
        tf.loadTrack(entry);

        Track track = tf.getTrackByName("foo", Genome.Assembly.hg19);


        for(int i = 0; i < track.getStarts().length; i++){
            System.out.println(track.getStarts()[i] + "\t" + track.getEnds()[i]);
        }
    }
}
