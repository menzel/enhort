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
package de.thm.run;

@Deprecated
public class Testruns {

    public static void main(String[] args) {






        //Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/projektphase/MLV.bed").toPath());
        //Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/projekphase/genomic_sites.sleeping_beauty.hg19.txt"));
        //Sites userDat = new UserData(new File("/home/menzel/Downloads/sites_andreas/copy.tab").toPath());

        /*
        Sites userDat = BackgroundModelFactory.createBackgroundModel(7489);

        logger.info(userDat.getPositionCount());


        Intersect intersect = new Intersect();
        TrackFactory.getInstance().loadIntervals();

        InOutTrack track = null;
        for(Track t: TrackFactory.getInstance().getAllIntervals()){

            if(t.getName().equals("Exons")){
                track = (InOutTrack) t;
            }
        }

        logger.info((Arrays.toString(intersect.getAverageDistance(track, userDat).toArray())));

        Analyse analyse = new Analyse();
        try {
            analyse.timing_intersect();
        } catch (Exception e) {
            logger.error("Exception {}", e.getMessage(), e);
        }
        */

    }
}
