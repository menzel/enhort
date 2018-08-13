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
package de.thm.result;

import de.thm.genomeData.tracks.TrackPackage;
import de.thm.misc.Genome;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DataViewResult implements Serializable, Result{


    private final Genome.Assembly assembly;
    private List<TrackPackage> packages;
    private Map<String, List<String>> cellLines;

    public DataViewResult(Genome.Assembly assembly, List<TrackPackage> packages, Map<String, List<String>> cellLines) {
        this.assembly = assembly;
        this.packages = packages;
        this.cellLines = cellLines;
    }

    @Override
    public Genome.Assembly getAssembly() {
        return this.assembly;
    }

    public List<TrackPackage> getPackages() {
        return packages;
    }

    public Map<String, List<String>> getCellLines() {
        return cellLines;
    }
}
