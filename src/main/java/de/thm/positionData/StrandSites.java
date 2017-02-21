package de.thm.positionData;

import java.util.ArrayList;

/**
 * Created by menzel on 2/21/17.
 */
public class StrandSites extends AbstractSites{
    private ArrayList<Character> strand;

    public ArrayList<Character> getStrand() {
        return strand;
    }

    public void setStrand(ArrayList<Character> strand) {
        this.strand = strand;
    }
}
