package de.thm.positionData;

import de.thm.misc.Genome;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssemblyGuesser {

    /**
     * Guess the assembly using Bedtools.
     * This methods needs the contigs files in the root dir with names like:
     * contigs_hg18, contigs_hg19, ...
     * And it needs the bedtools-suite installed
     *
     * Matching the known contigs from @see Genome.Assembly
     *
     * @param file - file to be read
     * @return the assembly or Assembly.Unknown
     */
    public static Genome.Assembly guessAssembly(Path file) {

        List<String> contigsPaths = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        List<String> assemblies = new ArrayList<>();

        for(Genome.Assembly assembly: Genome.Assembly.values())
            if(assembly != Genome.Assembly.Unknown)
                assemblies.add(assembly.toString());

        if (!System.getenv("HOME").contains("menzel"))
            assemblies.forEach(a -> contigsPaths.add("/home/mmnz21/con/contigs_" + a));
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


            } catch (Exception e){
                return Genome.Assembly.Unknown;
            }
        }

        for (int i = 0; i < counts.size(); i++)
            if ((int) counts.get(i) == Collections.min(counts))
                return Genome.Assembly.valueOf(assemblies.get(i));
        return Genome.Assembly.Unknown;
    }
}
