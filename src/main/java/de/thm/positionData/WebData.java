package de.thm.positionData;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Michael Menzel on 3/2/16.
 */
public class WebData extends Sites{

    private Date timestamp;
    private UUID uuid;

    public WebData(String jsonInput, Date timestamp) {
        this.timestamp = timestamp;
        this.uuid = UUID.randomUUID();

        String input = jsonInput.substring(1, jsonInput.length()-1);

        for(String p: input.split(","))
            this.positions.add(Long.parseLong(p));
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
