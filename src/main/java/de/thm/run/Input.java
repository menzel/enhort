package de.thm.run;

/**
 * Created by Michael Menzel on 3/2/16.
 */
public class Input {

    private final long id;
    private final String content;

    public Input(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
