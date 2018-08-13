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
