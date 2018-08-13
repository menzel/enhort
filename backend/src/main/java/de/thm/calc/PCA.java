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
package de.thm.calc;

import de.thm.result.ResultCollector;
import de.thm.stat.TestResult;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.*;

class PCA {

    private String[] names = new String[]{
            "P1",
            "P2",
            "P3",

            "P4",
            "P5",
            "Pout",
    };


    //TODO use track package name from db
    private List<String> tracknames = Arrays.asList("knownGenes.bed",
            "DNase_Cluster.bed",
            "Regulatory Regions",
            "cpg_islands.bed",
            "H3K4me3");

    private double[][] pointsArray = new double[][]{
            new double[]{1,5,4,3,1,6},
            new double[]{1,5,4,3,1,6},

            new double[]{3,1,6,5,0,10},
            new double[]{4,1,6,3,0,10},

            new double[]{4,0,19,6,1,9},
            new double[]{40,10,190,64,1,200}
    };

    void createBedPCA(ResultCollector collector) {
        List<TestResult> inOutResults = collector.getInOutResults(true);

        double[] user = inOutResults.stream()
                .filter(tr -> tracknames.contains(tr.getName()))
                .sorted(Comparator.comparing(TestResult::getName)) // sort by name
                .map(TestResult::getEffectSize)
                .mapToDouble(Double::doubleValue)
                .toArray();

        //pointsArray[pointsArray.length-1] = user; //TODO add user sites

        //create real matrix
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(pointsArray);

        //create covariance matrix of points, then find eigen vectors
        //see https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues

        Covariance covariance = new Covariance(realMatrix);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);

        SortedMap<String, double[]> pca = new TreeMap<>();

        for (int i = 0; i < names.length; i++)
            pca.put(names[i], new double[]{ed.getEigenvector(0).getEntry(i), ed.getEigenvector(1).getEntry(i)});
        //for (int i = 0; i < names.length; i++) pca.put(names[i], new double[]{ed.getEigenvector(i).getEntry(0), ed.getEigenvector(i).getEntry(1)});

        collector.setPCA(pca);
    }
}
