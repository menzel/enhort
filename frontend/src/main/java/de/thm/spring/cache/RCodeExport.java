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
package de.thm.spring.cache;

import de.thm.result.ResultCollector;
import de.thm.stat.TestResult;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Generates code to run locally in R to create a boxplot of the results
 */
public class RCodeExport {

    private final static String code = "\n"
            + "colnames(dat) <- c('p value','effect size','count positions measured inside','count positions expected inside ','percent positions measured inside','percent positions expected inside')\n"
            + "\n"
            + "#### Barplot ####\n"
            + "\n"
            + "#bottom, left, top and right margins\n"
            + "par(mar=c(15,5,5,5))\n"
            + "fontsize=1\n"
            + "\n"
            + "perc <- as.matrix(rbind(dat$`percent positions measured inside`,dat$`percent positions expected inside`))\n"
            + "colnames(perc) <- row.names(dat)\n"
            + "\n"
            + "mp <- barplot(perc,las=2,\n"
            + "              ylim=c(0,115),\n"
            + "              col = c('blue', 'lightgray'),\n"
            + "              #log='y',\n"
            + "              beside=TRUE,\n"
            + "              cex=fontsize,\n"
            + "              cex.axis=fontsize,\n"
            + "              cex.lab=fontsize,\n"
            + "              ylab = '% inside')\n"
            + "\n"
            + "\n"
            + "#### pvalues####\n"
            + "pval = 0.05 # significance level 5%\n"
            + "pvals <- dat$`p value` #pvalues\n"
            + "\n"
            + "#Plot stars above each significant bar group:\n"
            + "stars <- lapply(pvals,function(x)(ifelse(x < pval, '*', '')))\n"
            + "text(mp[1,] + 0.5, apply(perc,2,max)+2, stars,col='red', cex=2)\n"
            + "\n"
            + "#Or plot the p value for each two bars:\n"
            + "#text(mp[1,] + 0.5, apply(perc,2,max)+4, pvals,col='red')\n"
            + "\n"
            + "\n"
            + "#### Legend ####-\n"
            + "legend('topright', legend=c('% inside _filename_', '% inside control', 'p value < 0.05'), col=c('blue','lightgray','red'), bty='n', pch = c(15, 15, 8))\n"
            + "\n"
            + "\n"
            + "#### Vertical percent values, delete or comment out both lines to remove from plot ####\n"
            + "percVals <- lapply(perc, function(x) paste(toString(x[1]),'%'))\n"
            + "text(mp, apply(perc, 1:2, function(x) max(x-5,20)), percVals,col='coral',srt=90)\n"
            + "\n"
            + "\n"
            + "### column number warning ####\n"
            + "if (length(colnames(perc)) > 20) {\n"
            + "    title(main = 'You selected too many tracks in Enhort, \n try to run the analysis with fewer tracks to get a better plot.')\n"
            + "}";

    /**
     * Extracts data from the given collector to generate R plot code
     *
     * @param collector - collector to extract results from
     * @param filename - name of the file uploaded by the user to set for r plot legend
     *
     * @return fully working r code for users to run locally containing the results given by the collector
     */
    public static String barplot(ResultCollector collector, String filename) {

        String data = getData(collector);
        return data + code.replace("_filename_", filename);
    }


    private static String getData(ResultCollector collector) {
        Comparator<TestResult> byPackage = (t1, t2) -> (t1.getTrack().getPack().compareTo(t2.getTrack().getPack()));
        Comparator<TestResult> byName = (t1, t2) -> (t1.getTrack().getName().compareTo(t2.getTrack().getName()));

        Comparator<TestResult> resultComp = byPackage.thenComparing(byName);

        List<TestResult> results = collector.getInOutResults(true).stream()
                .sorted(resultComp)
                .filter(distinctByKey(TestResult::getTrack))
                .collect(Collectors.toList());

        String pval = "c(" + results.stream().map(TestResult::getpValue).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String es = "c(" + results.stream().map(TestResult::getEffectSize).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String meaIn = "c(" + results.stream().map(TestResult::getMeasuredIn).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String exIn = "c(" + results.stream().map(TestResult::getExpectedIn).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String meaInP = "c(" + results.stream().map(TestResult::getPercentInM).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String exInP = "c(" + results.stream().map(TestResult::getPercentInE).map(Object::toString).collect(Collectors.joining(", ")) + ")";

        String var = "dat = data.frame(";

        StringJoiner joiner = new StringJoiner(", ");

        for (TestResult s : results) {
            if (!s.getTrack().getCellLine().equals("Unknown"))
                joiner.add('"' + s.getName() + '(' + s.getTrack().getCellLine() + ')' + '"'); //add cell line to rowname if it is not unknown
            else
                joiner.add('"' + s.getName() + '"');
        }

        String header = "row.names(dat) <- c(" + joiner.toString() + ")";

        return String.join("\n", var, pval, es, meaIn, exIn, meaInP, exInP, ")\n", header);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, String> seen = new ConcurrentHashMap<>();
        return t -> seen.put(keyExtractor.apply(t), "") == null;
    }

}

