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

import de.thm.misc.Genome;

import java.util.ArrayList;
import java.util.List;

public class BatchResult implements Result {
    private final List<Result> results = new ArrayList<>();

    public void addResult(Result result) {
        results.add(result);
    }


    public List<Result> getResults() {
        return this.results;
    }

    @Override
    public Genome.Assembly getAssembly() {
        if (results.stream().map(Result::getAssembly).distinct().count() > 1)
            throw new RuntimeException("Assembly versions differ for list of results in batch result");

        return results.get(0).getAssembly();
    }
}
