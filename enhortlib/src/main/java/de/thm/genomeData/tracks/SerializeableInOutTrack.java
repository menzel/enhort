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

public class SerializeableInOutTrack implements Track {

    private static final long serialVersionUID = 30624952L;
    final int id;
    final int dbid;

    final long[] intervalsStart;
    final long[] intervalsEnd;
    final String name;
    final String description;
    final Genome.Assembly assembly;
    final String cellLine;

    final String pack;


    public SerializeableInOutTrack(long[] starts, long[] ends, String name, String description, Genome.Assembly assembly, String cellLine) {
        this.intervalsStart = starts;
        this.intervalsEnd = ends;
        this.name = name;
        this.description = description;
        this.assembly = assembly;
        this.cellLine = cellLine;

        this.pack = "None";
        this.id = -1;
        this.dbid = -1;
    }

    public InOutTrack getInOut() {
        return new InOutTrack(intervalsStart, intervalsEnd, name, description, assembly, cellLine);
    }

    @Override
    public Track clone() {
        throw new RuntimeException("Clone not implemented");
    }

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
        return id;
    }

    @Override
    public int getDbid() {
        return this.dbid;
    }

    @Override
    public String getDescription() {
        return description;
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
    public String getCellLine() {
        return cellLine;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getPack() {
        return pack;
    }

    public String getSource() {
        return "local";
    }

    public String getSourceurl() {
        return "local";
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result * name.hashCode();
        return result;
    }
}
