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

    final int id;
    final int dbid;

    transient final long[] intervalsStart;
    transient final long[] intervalsEnd;
    final String name;
    final String description;
    final Genome.Assembly assembly;
    final String cellLine;

    final String pack;

    final String source;
    final String sourceurl;

    AbstractTrack(long[] starts, long[] ends, TrackEntry entry) {

        this.intervalsStart = starts;
        this.intervalsEnd = ends;
        this.name = entry.getName();
        this.description = entry.getDescription();
        this.assembly = Genome.Assembly.valueOf(entry.getAssembly());
        this.cellLine = (entry.getCellline() == null || entry.getCellline().equals("")) ? "Unknown" : entry.getCellline();
        this.pack = entry.getPack();
        this.id = entry.getId();
        this.source = entry.getSource();
        this.sourceurl = entry.getSourceURL();
        this.dbid = entry.getId();
    }

    AbstractTrack(long[] starts, long[] ends, String name, String description, Genome.Assembly assembly, String cellLine) {
        this.intervalsStart = starts;
        this.intervalsEnd = ends;
        this.name = name;
        this.description = description;
        this.assembly = assembly;
        this.cellLine = cellLine;

        this.pack = "None";
        this.id = -1;
        this.dbid = -1;
        this.source = "local";
        this.sourceurl = "local";
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
    public int getDbid() {
        return dbid;
    }


    @Override
    public int getUid() {
        return id;
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

        desc += "<br> The original data source is " +
                ((source.length() == 0) ? "not specified." : source) +
                ((sourceurl.length() == 0) ? "" : " " + sourceurl + ".");

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

    public String getSource() {
        return source;
    }

    public String getSourceurl() {
        return sourceurl;
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result * name.hashCode();
        return result;
    }
}
