package de.thm.genomeData;

import de.thm.genomeData.sql.DBConnector;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.logo.GenomeFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by menzel on 7/3/17.
 */
public class TrackFactoryTest {

    private static final TrackFactory tf = TrackFactory.getInstance();
    private static int trackCounter = 0;

    @BeforeClass
    public static void getTracks() throws Exception {

        //reset track factory
        Class<?> inner = tf.getClass();
        Field instance = inner.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(tf,null);

        DBConnector connector = new DBConnector();
        connector.connect();
        connector.getAllTracks("WHERE (name like 'House%' OR name like 'known%' OR name like 'exons' OR name like 'contigs') AND assembly = 'hg19'").forEach(tf::loadTrack);

        List<Track> tmp  = tf.getTracks(GenomeFactory.Assembly.hg19);
        trackCounter = tmp.size();
        assertTrue(tmp.size() > 0);

        //set track package
        //TrackPackage pack = new TrackPackage(tmp, TrackPackage.PackageName.Basic, "basic tracks", GenomeFactory.Assembly.hg19, null);

        Field packs = inner.getDeclaredField("trackPackages");
        packs.setAccessible(true);
        //packs.set(tf, Collections.singletonList(pack));
    }

    @Test
    public void getTracksByPackage() throws Exception {
        //assertEquals(trackCounter, tf.getTracksByPackage(TrackPackage.PackageName.Basic, GenomeFactory.Assembly.hg19).size());
        //assertEquals(trackCounter, tf.getTracksByPackage("Basic", GenomeFactory.Assembly.hg19).size());
    }

    @Test
    public void getTrackPackageNames() throws Exception {
        //assertEquals("Basic", tf.getTrackPackageNames(GenomeFactory.Assembly.hg19).get(0));
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
    public void addAndCountTest() throws Exception {

        assertEquals(trackCounter,tf.getTracks(GenomeFactory.Assembly.hg19).size());
        assertEquals(trackCounter, tf.getTrackCount());

        Track track = mock(InOutTrack.class);

        int before = tf.getTrackCount();
        tf.addTrack(track);
        assertEquals(before+1, tf.getTrackCount());
    }
}