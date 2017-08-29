package de.thm.guess;

import de.thm.genomeData.tracks.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.UserData;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class AssemblyGuesserTest {

    private AssemblyGuesser guesser = new AssemblyGuesser();
    private String base = "/home/menzel/Desktop/THM/lfba/enhort/";

    @BeforeClass
    public static void setup(){
        String contigspath = "/home/menzel/Desktop/THM/lfba/enhort/dat/hg19/inout/contigs";

        try {
            TrackFactory.getInstance().loadTrack(new File(contigspath).toPath(), GenomeFactory.Assembly.hg19);
            TrackFactory.getInstance().loadTrack(new File(contigspath.replaceAll("19", "18")).toPath(), GenomeFactory.Assembly.hg18);
            TrackFactory.getInstance().loadTrack(new File(contigspath.replaceAll("19", "38")).toPath(), GenomeFactory.Assembly.hg38);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void guess_hg18_other() throws Exception {

        Path path = new File(base + "elAshkar/hg18.mlv_elAshkar.tab").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg18, guesser.guess(userData));
    }

    @Test
    public void guess_hg18_comb() throws Exception {

        Path path = new File(base + "elAshkar/hg18.mlv_comb.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg18, guesser.guess(userData));
    }


    @Test
    public void guess_hg19_other() throws Exception {

        Path path = new File(base + "elAshkar/hg19.mlv_comb.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg19, guesser.guess(userData));
    }



    @Test
    public void guess_hg18() throws Exception {

        Path path = new File(base + "testfiles/sb_hg18.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg18, guesser.guess(userData));
    }



    @Test
    public void guess_hg19() throws Exception {

        Path path = new File(base + "testfiles/sb_hg19.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg19, guesser.guess(userData));
    }

    @Test
    public void guess_hg38() throws Exception {

        Path path = new File(base + "testfiles/sb_hg38.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        assertEquals(GenomeFactory.Assembly.hg38, guesser.guess(userData));
    }

    @Test
    public void guess_hg17() throws Exception {

        Path path = new File(base + "testfiles/sb_hg17.bed").toPath();
        UserData userData = new UserData(GenomeFactory.Assembly.hg38, path);
        //TODO
        assertEquals(GenomeFactory.Assembly.hg18, guesser.guess(userData));
    }

}