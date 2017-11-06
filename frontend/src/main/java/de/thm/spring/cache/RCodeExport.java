package de.thm.spring.cache;

import de.thm.result.ResultCollector;
import de.thm.stat.TestResult;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Generates code to run locally in R to create a boxplot of the results
 */
public class RCodeExport {

    private final static String code = "\n"
            + "colnames(dat) <- c('p value','effect size','count positions measured inside','count positions expected inside ','percent positions measured inside','percent positions expected inside')\n"
            + "\n"
            + "filename <- 'of your Sites'\n"
            + "\n"
            + "#### Barplot ####\n"
            + "\n"
            + "#bottom, left, top and right margins\n"
            + "par(mar=c(15,5,5,5))\n"
            + "\n"
            + "perc <- as.matrix(rbind(dat$`percent positions measured inside`,dat$`percent positions expected inside`))\n"
            + "colnames(perc) <- row.names(dat)\n"
            + "\n"
            + "mp <- barplot(perc,las=2,\n"
            + "              ylim=c(0,115),\n"
            + "              col = c('blue', 'gray'),\n"
            + "              #log='y',\n"
            + "              beside=TRUE,\n"
            + "              ylab = '% inside')\n"
            + "\n"
            + "\n"
            + "#### pvalues####\n"
            + "pval = 0.05 # significance level 5%\n"
            + "pvals <- dat$`p value` #pvalues\n"
            + "\n"
            + "#Either plot stars above each significant bar group:\n"
            + "stars <- lapply(pvals,function(x)(ifelse(x < pval, '*', '')))\n"
            + "text(mp[1,] + 0.5, apply(perc,2,max)+2, stars,col='red', cex=2)\n"
            + "\n"
            + "#Or the p value for each bar group:\n"
            + "#text(mp[1,] + 0.5, apply(perc,2,max)+2, pvals,col='red')\n"
            + "\n"
            + "\n"
            + "#### Legend ####-\n"
            + "legend('topright', legend=c(paste('Inside counts ',filename), 'Inside counts random', '* Significant', 'Percent values'), fill=c('blue','gray','red', 'coral'),bty='n')\n"
            + "\n"
            + "\n"
            + "#### Vertical percent values ####\n"
            + "percVals <- lapply(perc, function(x) paste(toString(x[1]),'%'))\n"
            + "text(mp, apply(perc, 1:2, function(x) max(x-5,10)), percVals,col='coral',srt=90)\n";

    /**
     * Extracts data from the given collector to generate R plot code
     *
     * @param collector - collector to extract results from
     * @return fully working r code for users to run locally containing the results given by the collector
     */
    public static String barplot(ResultCollector collector) {

        String data = getData(collector);
        return data + code;

    }

    private static String getData(ResultCollector collector) {

        List<TestResult> results = collector.getInOutResults(true).stream()
                .sorted((t1, t2) -> (t2.getTrack().getPack().compareTo(t1.getTrack().getPack()))) //sort by package
                .collect(Collectors.toList());

        String pval = "c(" + results.stream().map(TestResult::getpValue).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String es = "c(" + results.stream().map(TestResult::getEffectSize).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String meaIn = "c(" + results.stream().map(TestResult::getMeasuredIn).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String exIn = "c(" + results.stream().map(TestResult::getExpectedIn).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String meaInP = "c(" + results.stream().map(TestResult::getPercentInM).map(Object::toString).collect(Collectors.joining(", ")) + "),";
        String exInP = "c(" + results.stream().map(TestResult::getPercentInE).map(Object::toString).collect(Collectors.joining(", ")) + ")";

        String var = "dat = data.frame(";

        String header = "row.names(dat) <- c(" + results.stream().map(TestResult::getName).map(s -> '"' + s + '"').collect(Collectors.joining(", ")) + ")";

        return String.join("\n", var, pval, es, meaIn, exIn, meaInP, exInP, ")\n", header);
    }
}

