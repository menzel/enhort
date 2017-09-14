package de.thm.guess;

import de.thm.genomeData.sql.DBConnector;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.UserData;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class AssemblyGuesserTest {

    private String base = "/home/menzel/Desktop/THM/lfba/enhort/";

    @BeforeClass
    public static void setup(){

        DBConnector connector = new DBConnector();
        connector.connect();
        connector.getAllTracks("WHERE name like 'contigs'").forEach(TrackFactory.getInstance()::loadTrack);
    }

    @Test
    public void guess_hg18_other() throws Exception {

        Path path = new File(base + "elAshkar/hg18.mlv_elAshkar.tab").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg18, AssemblyGuesser.guess(userData));
    }

    @Test
    public void guess_hg18_comb() throws Exception {

        Path path = new File(base + "elAshkar/hg18.mlv_comb.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg18, AssemblyGuesser.guess(userData));
    }


    @Test
    public void guess_hg19_other() throws Exception {

        Path path = new File(base + "elAshkar/hg19.mlv_comb.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg19, AssemblyGuesser.guess(userData));
    }



    @Test
    public void guess_hg18() throws Exception {

        Path path = new File(base + "testfiles/sb_hg18.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg18, AssemblyGuesser.guess(userData));
    }



    @Test
    public void guess_hg19() throws Exception {

        Path path = new File(base + "testfiles/sb_hg19.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg19, AssemblyGuesser.guess(userData));
    }

    @Test
    public void guess_hg38() throws Exception {

        Path path = new File(base + "testfiles/sb_hg38.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg38, AssemblyGuesser.guess(userData));
    }

    @Test
    public void guess_hg17() throws Exception {

        Path path = new File(base + "testfiles/sb_hg17.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        //TODO
        assertEquals(GenomeFactory.Assembly.hg18, AssemblyGuesser.guess(userData));
    }

}