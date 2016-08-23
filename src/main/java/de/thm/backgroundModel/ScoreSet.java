package de.thm.backgroundModel;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by menzel on 8/23/16.
 */
public class ScoreSet {

    private Double[] scoreList;
    private int i;

    public ScoreSet(int size){

        scoreList = new Double[size];
        i = 0;
    }

    public void add(Double score){
        if(i < scoreList.length)
            scoreList[i++] = score;
        else
            System.err.println("ScoreSet cannot accept another score " + Arrays.toString(scoreList));
    }

    public Double[] getScores() {
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
