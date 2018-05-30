package de.thm.misc;

import de.thm.genomeData.sql.DBConnector;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.run.BackendServer;
import org.junit.Test;

import java.io.File;

public class OffsetTest {

    @Test
    public void testOffset() throws Exception {

        TrackFactory tf = TrackFactory.getInstance();

        BackendServer.dbfilepath = "/home/menzel/Desktop/THM/lfba/enhort/stefan.db";
        BackendServer.basePath = new File("/home/menzel/Desktop/THM/lfba/enhort/dat/stefan/").toPath();

        DBConnector connector = new DBConnector();
        connector.connect();
        connector.getAllTracks("WHERE name = 'Contigs' AND genome_assembly = 'hg19'")
                .forEach(tf::loadTrack);

        Track track = tf.getTrackByName("contigs", Genome.Assembly.hg19);

        for(int i = 0; i < track.getStarts().length; i++){
            //TODO System.out.println(track.getStarts()[i] + "\t" + track.getEnds()[i]);
        }
    }
}
