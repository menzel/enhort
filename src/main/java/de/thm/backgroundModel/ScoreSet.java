package de.thm.backgroundModel;

import java.util.Arrays;
import java.util.Objects;

/**
 * Holds a set of scores to reference to the intermediate interval in scored bg models
 * Created by menzel on 8/23/16.
 */
class ScoreSet {

    private Double[] scoreList; // list of scores

    /**
     * Constructor for testing
     */
    ScoreSet(Double[] values){
        scoreList = values;
    }

    ScoreSet(int size){

        scoreList = new Double[size];
    }

    /**
     * Adds a score to the set
     *
     * @param score to set
     */
    void add(Double score, int i){
        if(i < scoreList.length)
            scoreList[i] = score;
        else
            System.err.println("ScoreSet cannot accept another score " + Arrays.toString(scoreList));
    }

    Double[] getScores() {
        return scoreList;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreSet other = (ScoreSet) o;

        for(int j = 0; j < scoreList.length; j++){
            if(!Objects.equals(this.scoreList[j], other.getScores()[j])){
                return false;
            }
        }
        return true;



    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(scoreList);
    }
}
