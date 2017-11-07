package de.thm.genomeData.tracks;


import de.thm.misc.Genome;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract skeletal class.
 * <p>
 * Created by Michael Menzel on 24/2/16.
 */
@SuppressWarnings("unused")
public abstract class AbstractTrack implements Track {

    private static final AtomicInteger UID = new AtomicInteger(1);
    private static final long serialVersionUID = 30624951L;
    final int uid = UID.incrementAndGet();

    transient final long[] intervalsStart;
    transient final long[] intervalsEnd;
    final String name;
    final String description;
    final Genome.Assembly assembly;
    final String cellLine;

    final String pack;

    String source = "";
    String sourceurl = "";

    AbstractTrack(long[] starts, long[] ends, String name, String description, Genome.Assembly assembly, String cellLine, String pack) {

        this.intervalsStart = starts;
        this.intervalsEnd = ends;
        this.name = name;
        this.description = description;
        this.assembly = assembly;
        this.cellLine = cellLine;
        this.pack = pack;
    }

    AbstractTrack(long[] starts, long[] ends, String name, String description, Genome.Assembly assembly, String cellLine) {
        this.intervalsStart = starts;
        this.intervalsEnd = ends;
        this.name = name;
        this.description = description;
        this.assembly = assembly;
        this.cellLine = cellLine;
        this.pack = "None";
    }



    @Override
    public abstract Track clone();

    @Override
    public long[] getEnds() {
        return intervalsEnd;
    }

    @Override
    public long[] getStarts() {
        return intervalsStart;
    }


    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public String getDescription() {
        String desc;

        if (description.length() < 2) {
            if (cellLine.contains("Unknown"))
                desc = "The track " + name + " is not cell line specific (" + assembly + ").";
            else
                desc = "The track " + name + " is taken from the cell line " + cellLine + " (" + assembly + ").";
        } else {
            desc = description + "<br> Cell line " + cellLine + " (" + assembly + ").";
        }

        desc += "<br> The original data source is " + ((source.length() == 0) ? "not specified." : source) + ((sourceurl.length() == 0) ? "" : " " + sourceurl + ".");

        return desc;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Genome.Assembly getAssembly() {
        return assembly;
    }


    @Override
    public String getCellLine() { return cellLine;
    }

    @Override
    public String toString(){
        return name;
    }

    public String getPack() {
        return pack;
    }

}
