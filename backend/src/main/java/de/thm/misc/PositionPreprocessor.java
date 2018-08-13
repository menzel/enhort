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

import de.thm.genomeData.tracks.NamedTrack;
import de.thm.genomeData.tracks.ScoredTrack;
import de.thm.genomeData.tracks.TrackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data preprocessor for merging overlaping tracks
 * <p>
 * Created by Michael Menzel on 8/12/15.
 */
public final class PositionPreprocessor {

    private static final Logger logger = LoggerFactory.getLogger(PositionPreprocessor.class);

    public static ScoredTrack preprocessData(ScoredTrack track) {
        List<Long> newStart = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();
        List<Double> newScore = new ArrayList<>();

        List<Long> tracksStart = Arrays.stream(track.getStarts()).boxed().collect(Collectors.toList());
        List<Long> tracksEnd = Arrays.stream(track.getEnds()).boxed().collect(Collectors.toList());
        List<Double> scores = Arrays.stream(track.getIntervalScore()).boxed().collect(Collectors.toList());

        if (tracksStart.isEmpty()) return track;

        long start = tracksStart.get(0);
        long end = tracksEnd.get(0);

        double tmpScore = scores.get(0);
        int iCount = 1;


        for (int i = 0; i < tracksStart.size(); i++) {

            if (i < tracksStart.size() - 1 && end > tracksStart.get(i + 1)) { // overlap

                if (end < tracksEnd.get(i + 1))
                    end = tracksEnd.get(i + 1);

                tmpScore += scores.get(i + 1); // TODO use max instead of average
                iCount++;

            } else {  //do not overlap
                newStart.add(start);
                newEnd.add(end);
                newScore.add(tmpScore / iCount);

                if (i >= tracksStart.size() - 1) break; // do not get next points if this was the last

                start = tracksStart.get(i + 1);
                end = tracksEnd.get(i + 1);
                tmpScore = scores.get(i + 1);
                iCount = 1;

            }
        }

        tracksStart.clear();
        tracksEnd.clear();

        ScoredTrack tmp = TrackFactory.getInstance().createScoredTrack(newStart,
                newEnd,
                Arrays.stream(track.getIntervalName()).collect(Collectors.toList()).subList(0, newStart.size()),
                newScore,
                track.getName(),
                track.getDescription(),
                track.getSource(),
                track.getSourceurl(),
                track.getAssembly(),
                track.getCellLine()
        );

        //tmp = Tracks.bin(tmp, 50);

        return tmp;
    }



    public static NamedTrack preprocessData(NamedTrack track) {
        List<Long> newStart = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();
        List<String> newNames = new ArrayList<>();

        List<Long> tracksStart; //= track.getStarts();
        List<Long> tracksEnd; // = track.getEnds();
        List<String> names; // = track.getIntervalName();

        //if (tracksStart.isEmpty()) return track;
        if(true) return track; // do not preprocess Named Track for now. TODO Fix

        long start = tracksStart.get(0);
        long end = tracksEnd.get(0);
        String name = names.get(0);

        for (int i = 0; i < tracksStart.size(); i++) {

            if (i < tracksStart.size() - 1 && end > tracksStart.get(i + 1)) { // overlap

                if (end < tracksEnd.get(i + 1))
                    end = tracksEnd.get(i + 1);

                name = name.equals(names.get(i)) ? name : names.get(i) + "_" + name;

            } else {  //do not overlap
                newStart.add(start);
                newEnd.add(end);
                newNames.add(name.intern());

                if (i >= tracksStart.size() - 1) break; // do not get next points if this was the last

                start = tracksStart.get(i + 1);
                end = tracksEnd.get(i + 1);
                name = names.get(i + 1);

            }
        }

        tracksStart.clear();
        tracksEnd.clear();


        return TrackFactory.getInstance().createNamedTrack(newStart, newEnd, newNames,track.getName(), track.getDescription(), track.getAssembly(), track.getCellLine());
    }
}
