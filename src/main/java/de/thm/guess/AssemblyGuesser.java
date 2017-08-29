package de.thm.guess;

import de.thm.calc.Intersect;
import de.thm.calc.TestTrackResult;
import de.thm.genomeData.tracks.InOutTrack;
import de.thm.genomeData.tracks.TrackFactory;
import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssemblyGuesser {

    public static GenomeFactory.Assembly guess(Sites sites){

        Intersect<InOutTrack> intersect = new Intersect<>();

        return Arrays.stream(GenomeFactory.Assembly.values())
                    .filter(assembly -> assembly != GenomeFactory.Assembly.Unknown)
                    .map(assembly -> TrackFactory.getInstance()
                        .getTrackByName("contigs", assembly))
                    .map(track -> (InOutTrack) track)
                    .map(track -> intersect.searchSingleInterval(track, sites))
                    .peek(ttr -> System.out.println(ttr.getUsedTrack().getAssembly() + " " + ttr.getOut()))
                    .sorted(Comparator.comparingInt(TestTrackResult::getOut))
                    .findFirst()
                    .get().getUsedTrack().getAssembly();
    }

    /**
     * Guess the assembly using Bedtools.
     * This methods needs the contigs files in the root dir with names like:
     * contigs_hg18, contigs_hg19, ...
     * And it needs the bedtools-suite installed
     *
     * Matching the known contigs from @see GenomeFactory.Assembly
     *
     * @param file - file to be read
     * @return the assembly or Assembly.Unknown
     */
    public static GenomeFactory.Assembly guessAssembly(Path file) {

        List<String> contigsPaths = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        List<String> assemblies = new ArrayList<>();

        for(GenomeFactory.Assembly assembly: GenomeFactory.Assembly.values())
            if(assembly != GenomeFactory.Assembly.Unknown)
                assemblies.add(assembly.toString());

        if (!System.getenv("HOME").contains("menzel"))
            assemblies.forEach(a -> contigsPaths.add("contigs_" + a));
        else //on local pc:
            assemblies.forEach(a -> contigsPaths.add( "../dat/" + a + "/inout/contigs"));

        Pattern p = Pattern.compile("[\r\n]");

        for(String filename: contigsPaths) {
            try {
                String command = "bedtools intersect -v -a "+ file.toAbsolutePath() + " -b " + filename;
                DefaultExecutor exe = new DefaultExecutor();

                ByteArrayOutputStream stdout = new ByteArrayOutputStream();
                ByteArrayOutputStream stderr = new ByteArrayOutputStream();
                PumpStreamHandler psh = new PumpStreamHandler(stdout, stderr);

                exe.setStreamHandler(psh);
                exe.execute(CommandLine.parse(command));

                //count the lines returned by bedtools:
                Matcher m = p.matcher(stdout.toString());
                int lines = 0;
                while(m.find()) lines++;
                counts.add(lines);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < counts.size(); i++)
            if ((int) counts.get(i) == Collections.min(counts))
                return GenomeFactory.Assembly.valueOf(assemblies.get(i));
        return GenomeFactory.Assembly.Unknown;
    }
}
