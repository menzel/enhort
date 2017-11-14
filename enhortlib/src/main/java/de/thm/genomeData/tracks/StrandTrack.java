package de.thm.genomeData.tracks;

import de.thm.misc.Genome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Strand track, which is a inout track with strand (sense antisenes -+) information
 * The strand data is + -  or 'o' for both strands
 *
 * Created by menzel on 2/21/17.
 */
public class StrandTrack extends AbstractTrack {

    private final char[] strand; //should only contain  - + o  (o for both strands)

    private final Logger logger = LoggerFactory.getLogger(StrandTrack.class);

    StrandTrack(List<Long> starts, List<Long> ends, List<Character> strand, TrackEntry entry) {

        super(starts.stream().mapToLong(l->l).toArray(),
                ends.stream().mapToLong(l->l).toArray(),
                entry);

        if (strand != null) {
            this.strand = new char[strand.size()];
            for (int i = 0; i < strand.size(); i++)
                this.strand[i] = strand.get(i);
        } else this.strand = new char[0];

        if(intervalsStart.length != intervalsEnd.length || intervalsEnd.length  != this.strand.length){
            logger.warn("In StrandTrack " + name + " some interval data is missing");
        }
    }


    StrandTrack(long[] starts, long[] ends, char[] strand, String name, String description, Genome.Assembly assembly, String cellLine) {

        super(starts, ends, name, description, assembly, cellLine);

        if (strand != null) {
            this.strand = strand;
        } else this.strand = new char[0];


        if (intervalsStart.length != intervalsEnd.length || intervalsEnd.length != this.strand.length) {
            logger.warn("In StrandTrack " + name + " some interval data is missing");
        }
    }



    @Override
    public Track clone() {

        return new StrandTrack(
                this.getStarts(),
                this.getEnds(),
                this.getStrands(),
                name,
                description,
                assembly,
                cellLine);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StrandTrack)) return false;

        StrandTrack interval = (StrandTrack) o;
        if (!Arrays.equals(intervalsStart, interval.intervalsStart)) return false;
        if (!Arrays.equals(intervalsEnd, interval.intervalsEnd)) return false;
        return !(description != null ? !description.equals(interval.description) : interval.description != null);
    }

    public char[] getStrands() {
        return strand;
    }

    public InOutTrack getInOut() {
        return new InOutTrack(intervalsStart, intervalsEnd, this.name, this.description, this.assembly, this.cellLine);
    }
}
