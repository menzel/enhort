package de.thm.genomeData.tracks;

import de.thm.logo.GenomeFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Strand track, which is a inout track with strand (sense antisenes -+) information
 * The strand data is + -  or 'o' for both strands
 *
 * Created by menzel on 2/21/17.
 */
public class StrandTrack extends Track{

    private final int uid = UID.incrementAndGet();
    private final long[] intervalsStart;
    private final long[] intervalsEnd;
    private final char[] strand; //should only contain  - + o  (o for both strands)
    private final String name;
    private final GenomeFactory.Assembly assembly;
    private final String cellLine;
    private final String description;

    StrandTrack(List<Long> starts, List<Long> ends, List<Character> strand, String name, String description, GenomeFactory.Assembly assembly, String cellLine) {

        if (starts != null) {
            intervalsStart = new long[starts.size()];
            for (int i = 0; i < starts.size(); i++)
                intervalsStart[i] = starts.get(i);
        } else intervalsStart = new long[0];

        if (ends != null) {
            intervalsEnd = new long[ends.size()];
            for (int i = 0; i < ends.size(); i++)
                intervalsEnd[i] = ends.get(i);
        } else intervalsEnd= new long[0];

        if (strand != null) {
            this.strand = new char[strand.size()];
            for (int i = 0; i < strand.size(); i++)
                this.strand[i] = strand.get(i);
        } else this.strand = new char[0];


        if(intervalsStart.length != intervalsEnd.length || intervalsEnd.length  != this.strand.length){
            System.err.println("In StrandTrack " + name + " some interval data is missing");
        }


        this.description = description;
        this.name = name;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }


    StrandTrack(long[] starts, long[] ends, char[] strand, String name, String description, GenomeFactory.Assembly assembly, String cellLine) {

        if (starts != null) {
            this.intervalsStart = starts;
        } else intervalsStart = new long[0];

        if (ends != null) {
            intervalsEnd = ends;
        } else intervalsEnd = new long[0];

        if (strand != null) {
            this.strand = strand;
        } else this.strand = new char[0];


        if (intervalsStart.length != intervalsEnd.length || intervalsEnd.length != this.strand.length) {
            System.err.println("In StrandTrack " + name + " some interval data is missing");
        }


        this.description = description;
        this.name = name;
        this.assembly = assembly;
        this.cellLine = cellLine;
    }



    @Override
    public int getUid() {
        return uid;
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
                cellLine
        );
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

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + intervalsEnd.length;
        return result;
    }

    @Override
    public long[] getStarts() {
        return this.intervalsStart;
    }

    @Override
    public long[] getEnds() {
        return this.intervalsEnd;
    }


    public char[] getStrands() {
        return strand;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public GenomeFactory.Assembly getAssembly() {
        return this.assembly;
    }

    @Override
    public String getCellLine() {
        return this.cellLine;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public InOutTrack getInOut() {
        return new InOutTrack(intervalsStart, intervalsEnd, this.name, this.description, this.assembly, this.cellLine);
    }
}
