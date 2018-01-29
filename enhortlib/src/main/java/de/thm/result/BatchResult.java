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
        return null;
    }
}
