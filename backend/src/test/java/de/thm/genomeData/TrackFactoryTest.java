package de.thm.genomeData;

import de.thm.genomeData.sql.DBConnector;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.Genome;
import de.thm.run.BackendServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created by menzel on 7/3/17.
 */
public class TrackFactoryTest {

    private static final TrackFactory tf = TrackFactory.getInstance();
    private static int trackCounter = 0;

    @BeforeClass
    public static void getTracks() throws Exception {

        BackendServer.dbfilepath = "/home/menzel/Desktop/THM/lfba/enhort/stefan.db";
        BackendServer.basePath = new File("/home/menzel/Desktop/THM/lfba/enhort/dat/stefan/").toPath();


        DBConnector connector = new DBConnector();
        connector.connect();
        connector.getAllTracks("WHERE (name like 'House%' OR name like 'known%' OR name like 'contigs') AND genome_assembly = 'hg19'")
                .forEach(tf::loadTrack);
        trackCounter = tf.getTracks(Genome.Assembly.hg19).size();

        if (trackCounter < 1)
            throw new Exception("No tracks loaded for TrackFactoryTest");
    }

    @Test
    public void getTracksByPackage() throws Exception {
        //assertEquals(trackCounter, tf.getTracksByCellline(TrackPackage.PackageName.Basic, Genome.Assembly.hg19).size());
        //assertEquals(trackCounter, tf.getTracksByCellline("Basic", Genome.Assembly.hg19).size());
    }

    @Test
    public void getTrackPackageNames() throws Exception {
        //assertEquals("Basic", tf.getTrackPackageNames(Genome.Assembly.hg19).get(0));
    }

    @Test
    public void getTrackById() throws Exception {
        Track track = tf.getTrackByName("Contigs", Genome.Assembly.hg19);

        Track result = tf.getTrackById(track.getUid());

        assertEquals("Contigs", result.getName());
    }

    @Test
    public void getTracksById() throws Exception {
        List<String> ids = tf.getTracks(Genome.Assembly.hg19).stream()
                .map(t -> Integer.toString(t.getUid()))
                .collect(Collectors.toList());

        List<Track> tracks = tf.getTracksById(ids);

        assertEquals(tf.getTrackCount(), tracks.size());
    }


    @Test
    public void getTrackByName() throws Exception {
        Track track = tf.getTrackByName("Contigs", Genome.Assembly.hg19);
        assertEquals("Contigs", track.getName());
    }

    @Test(expected = RuntimeException.class)
    public void getTrackByNameException() throws Exception {
        tf.getTrackByName("notaname", Genome.Assembly.hg19);
    }
}