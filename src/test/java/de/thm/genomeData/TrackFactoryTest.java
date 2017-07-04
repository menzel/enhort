package de.thm.genomeData;

import de.thm.logo.GenomeFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by menzel on 7/3/17.
 */
public class TrackFactoryTest {

    private static final Path basePath = new File("/home/menzel/Desktop/THM/lfba/enhort/dat/hg19").toPath();
    private static final TrackFactory tf = TrackFactory.getInstance();
    private static int trackCounter = 0;

    @BeforeClass
    public static void getTracks() throws Exception {
        List<Track> tmp = tf.getTracks(basePath.resolve("inout"), TrackFactory.Type.inout, GenomeFactory.Assembly.hg19);
        trackCounter = tmp.size();
        assertTrue(tmp.size() > 0);
        tmp.forEach(tf::addTrack); // add to tf
    }

    @Test
    public void loadTrack() throws Exception {
    }

    @Test
    public void loadTracks() throws Exception {
    }

    @Test
    public void getTracksByPackage() throws Exception {
    }

    @Test
    public void getTracksByPackage1() throws Exception {
    }

    @Test
    public void getTrackPackageNames() throws Exception {
    }

    @Test
    public void getTrackById() throws Exception {
        Track track = tf.getTrackByName("Contigs", GenomeFactory.Assembly.hg19);

        Track result = tf.getTrackById(track.getUid());

        assertEquals("Contigs", result.getName());
    }

    @Test
    public void getTrackByName() throws Exception {
        Track track = tf.getTrackByName("Contigs", GenomeFactory.Assembly.hg19);
        assertEquals("Contigs", track.getName());
    }

    @Test(expected = RuntimeException.class)
    public void getTrackByNameException() throws Exception {
        tf.getTrackByName("notaname", GenomeFactory.Assembly.hg19);
    }

    @Test
    public void getTrackCount() throws Exception {
        assertEquals(trackCounter, tf.getTrackCount());
    }

    @Test
    public void addTrack() throws Exception {
        Track track = mock(InOutTrack.class);

        int before = tf.getTrackCount();
        tf.addTrack(track);
        assertEquals(before+1, tf.getTrackCount());
    }
}