// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
package de.thm.precalc;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.sql.DBConnector;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.logo.Logo;
import de.thm.logo.LogoCreator;
import de.thm.logo.Sequencelogo;
import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import de.thm.run.BackendServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by menzel on 2/9/17.
 */
public class SiteFactoryTest {

    private static SiteFactory factory;

    @BeforeClass
    public static void init(){

        BackendServer.dbfilepath = "/home/menzel/Desktop/THM/lfba/enhort/stefan.db";

        DBConnector connector = new DBConnector();
        connector.connect();
        connector.getAllTracks("WHERE name like 'known genes' AND genome_assembly = 'hg19'").forEach(TrackFactory.getInstance()::loadTrack);

        //mock contigs track
        Track contigs = mock(InOutTrack.class);
        when(contigs.getStarts()).thenReturn(new long[]{0});
        when(contigs.getEnds()).thenReturn(new long[]{2897310462L});
        when(contigs.getAssembly()).thenReturn(Genome.Assembly.hg19);
        when(contigs.getName()).thenReturn("Contigs");

        TrackFactory trackFactory = TrackFactory.getInstance();
        trackFactory.addTrack(contigs);
        //end mock contigs track

        factory = new SiteFactory(Genome.Assembly.hg19, 1000000);
    }


    @Test
    public void score() throws Exception {
        List<String> sequences = new ArrayList<>();

        sequences.add("CCAT");
        sequences.add("CTAT");
        sequences.add("CTAA");
        sequences.add("CCAT");

        Logo sequencelogo = LogoCreator.createLogo(sequences);


        List<String> seq = new ArrayList<>();
        seq.add("CCAT");
        seq.add("GGGG");
        seq.add("GGAG");

        Map<String, Double> results = factory.calculateScores(sequencelogo, seq);

        assertEquals(1.0, results.get("CCAT"), 0.1);
        assertEquals(0.0, results.get("GGGG"), 0.1);
        assertEquals(.1, results.get("GGAG"), 0.1);

        List<String> sequences2 = new ArrayList<>();
        sequences2.add("TA");
        sequences2.add("TA");

        Logo sequencelogo2 = LogoCreator.createLogo(sequences2);
        assertEquals(1., factory.score(sequencelogo2, "TA"), 0.1);
    }

    //@Test // is disabled because in SiteCreator.java (line 33) the init of the index table is disalbed, too. But it should work
    public void getSites() throws Exception {
        InOutTrack track = (InOutTrack) TrackFactory.getInstance().getTracks(Genome.Assembly.hg19).get(0);

        int in = 50;
        int out = 100;

        if(track == null)
            return;

        List<Long> pos = factory.getSites(track, in, out).getPositions();

        Sites sites = new Sites() {
            @Override
            public void addPositions(Collection<Long> values) {}

            @Override
            public List<Long> getPositions() {
                return pos;
            }

            @Override
            public void setPositions(List<Long> positions) {}

            @Override
            public List<Character> getStrands() {
                return null;
            }

            @Override
            public int getPositionCount() {
                return in+out;
            }

            @Override
            public Genome.Assembly getAssembly() {
                return Genome.Assembly.hg19;
            }

            @Override
            public String getCellline() {
                return null;
            }
        };

        Intersect<InOutTrack> intersect = new Intersect<>();

        TestTrackResult result = intersect.searchSingleInterval(track, sites);

        assertEquals(result.getIn(), in);
        assertEquals(result.getOut(), out);
    }


    // @Test // disabled b/c it is not for testing, but for creating data
    public void csaba() throws Exception {

        List<String> sequences = Files.readAllLines(new File("/home/menzel/Desktop/THM/lfba/projekte/csaba/L1TAinsertionSites_JulijaRaizPaper.csv").toPath());
        sequences = sequences.stream().map(String::toUpperCase).map(i -> i.substring(2,10)).collect(Collectors.toList());


        Logo sequencelogo = LogoCreator.createLogo(sequences);

        List<Long> sites = factory.getByLogo(sequencelogo, 100000).getPositions();
        List<String> sitesNew = new ArrayList<>();


        for(Long site: sites){
            String line = ChromosomSizes.getInstance().mapToChr(Genome.Assembly.hg19,site).toString();

            line = line.substring(1, line.length()-1);
            line = line.replace(',','\t');

            sitesNew.add(line);
        }


        Files.write(Paths.get("/home/menzel/Desktop/sites.bed"), sitesNew);

        List<String> seq = GenomeFactory.getInstance().getSequence(Genome.Assembly.hg19, sites,8, Integer.MAX_VALUE);
        Logo newLogo = LogoCreator.createLogo(seq);


        try {
            Files.write(Paths.get("/home/menzel/Desktop/test.out"), seq);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void getByLogo2() throws Exception {
        List<String> sequences = new ArrayList<>();

        sequences.add("AATTAATT");
        sequences.add("AACTAATT");
        sequences.add("AATTATTT");
        sequences.add("AATTATTT");
        sequences.add("AATGAAAT");

        Logo sequencelogo = LogoCreator.createLogo(sequences);

        List<Long> sites = factory.getByLogo(sequencelogo, 10).getPositions();
        List<String> seq = GenomeFactory.getInstance().getSequence(Genome.Assembly.hg19, sites,4, Integer.MAX_VALUE);
        Logo newLogo = LogoCreator.createLogo(seq);


        System.out.println(newLogo.getConsensus());

        assertEquals(newLogo.getConsensus(), "TTAA");
    }






    @Test
    public void getByLogo() throws Exception {
        List<String> sequences = new ArrayList<>();

        sequences.add("AATT");
        sequences.add("AACT");
        sequences.add("AATT");
        sequences.add("AATT");
        sequences.add("AATG");

        Logo sequencelogo = LogoCreator.createLogo(sequences);

        List<Long> sites = factory.getByLogo(sequencelogo, 100).getPositions();
        List<String> seq = GenomeFactory.getInstance().getSequence(Genome.Assembly.hg19, sites,4, Integer.MAX_VALUE);
        Logo newLogo = LogoCreator.createLogo(seq);

        assertEquals(newLogo.getConsensus(), "AATT");
    }


    @Test
    public void getSitesByLogo() throws Exception {

        //mock sequencelogo
        Logo sequencelogo = new Sequencelogo();

        Map<String, Double> a = new HashMap<>();
        Map<String, Double> b = new HashMap<>();

        a.put("T",1d);
        a.put("A",0.5);
        b.put("A",2d);

        sequencelogo.add(a);
        sequencelogo.add(b);

        List<Long> sites = factory.getByLogo(sequencelogo, 10).getPositions();
        List<String> seq = GenomeFactory.getInstance().getSequence(Genome.Assembly.hg19, sites,4, Integer.MAX_VALUE);

        assert seq != null;
        assertEquals(seq.size(), 10);

        Logo newLogo = LogoCreator.createLogo(seq);

        //compare newLogo and sequencelogo

        //System.out.println(sequencelogo.getConsensus());
        //System.out.println(newLogo.getConsensus());
    }
}