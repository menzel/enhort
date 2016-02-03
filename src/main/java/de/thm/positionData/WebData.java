package de.thm.positionData;

/**
 * Created by Michael Menzel on 3/2/16.
 */
public class WebData extends Sites{

    public WebData(String jsonInput) {
        String input = jsonInput.substring(1, jsonInput.length()-1);

        for(String p: input.split(","))
            this.positions.add(Long.parseLong(p));
    }
}
