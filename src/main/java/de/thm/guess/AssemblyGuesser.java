package de.thm.guess;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.InOutTrack;
import de.thm.genomeData.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;

import java.util.Arrays;
import java.util.Comparator;

public class AssemblyGuesser {

    public GenomeFactory.Assembly guess(Sites sites){

        Intersect<InOutTrack> intersect = new Intersect<>();

        return Arrays.stream(GenomeFactory.Assembly.values())
                    .map(assembly -> TrackFactory.getInstance()
                    .getTrackByName("Contigs", assembly))
                    .map(track -> (InOutTrack) track)
                    .map(track -> intersect.searchSingleInterval(track, sites))
                    .sorted(Comparator.comparingInt(TestTrackResult::getOut))
                    .findFirst()
                    .get().getUsedTrack().getAssembly();
    }
}
