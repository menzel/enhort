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
package de.thm.safe_harbor;

import de.thm.backgroundModel.BackgroundModelFactory;
import de.thm.calc.CalcCaller;
import de.thm.genomeData.tracks.Track;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.misc.Genome;
import de.thm.positionData.Sites;
import de.thm.result.ResultCollector;
import de.thm.stat.TestResult;
import de.thm.teststubs.UserData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by menzel on 5/7/17.
 */
public class safe_harbor_plot_all {

    //@Test
    public void getPlotData() {

        TrackFactory.getInstance().loadAllTracks();

        // get file list

        Path path = new File("/home/menzel/Desktop/THM/lfba/projekte/safe_harbor/files").toPath();

        List<Path> files = new ArrayList<>();

        try {
            Files.walk(Paths.get(path.toString())).filter(Files::isRegularFile).forEach(files::add);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // collect tracks

        List<Track> tracks = new ArrayList<>();

        tracks.addAll(TrackFactory.getInstance().getTracksByCellline("Basic", Genome.Assembly.hg19));
        tracks.addAll(TrackFactory.getInstance().getTracksByCellline("Cancer", Genome.Assembly.hg19));
        tracks.addAll(TrackFactory.getInstance().getTracksByCellline("safe_harbor", Genome.Assembly.hg19));


        // run analysis for each file and print

        Sites bg = BackgroundModelFactory.createBackgroundModel(Genome.Assembly.hg19, 30000);


        boolean header = true;

        for (Path file : files) {

            CalcCaller multi = new CalcCaller();
            Sites sites = new UserData(Genome.Assembly.hg19, file);
            ResultCollector collector = multi.execute(tracks, sites, bg, false);


            List<TestResult> results = collector.getResults().stream()
                    .sorted(Comparator.comparing(TestResult::getName))
                    .collect(Collectors.toList());

            if (header) {
                results.forEach(s -> System.out.print(s.getName() + ","));
                System.out.println();
                header = false;
            }


            if (results.size() != tracks.size()) {
                System.err.println(file.getFileName() + " " + results.size());
                continue;
            }


            System.out.print(file.getFileName() + ",");
            for (TestResult result : results)
                if (result.getPercentInE() > result.getPercentInM())  // weniger als erwartet drinn
                    System.out.print((1 / (result.getEffectSize()) + 0.001) + ",");
                else
                    System.out.print((result.getEffectSize() + 0.001) + ",");

            System.out.println();

            System.out.print(file.getFileName() + "_pvalue,");
            for (TestResult result : results)
                System.out.print(result.getpValue() + ",");

            System.out.println();
        }

    }
}
